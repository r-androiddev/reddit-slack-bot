package io.dwak.redditslackbot.reddit

import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.extension.PairUtil
import io.dwak.redditslackbot.extension.toTriple
import io.dwak.redditslackbot.inject.annotation.qualifier.AppConfig
import io.dwak.redditslackbot.inject.annotation.qualifier.reddit.RedditConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import io.dwak.redditslackbot.reddit.model.CannedResponse
import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.reddit.model.isSelfPost
import io.dwak.redditslackbot.reddit.model.isSuspiciousPost
import io.dwak.redditslackbot.reddit.network.service.RedditLoginService
import io.dwak.redditslackbot.reddit.network.service.RedditService
import io.dwak.redditslackbot.slack.SlackBot
import io.dwak.redditslackbot.slack.model.ButtonAction
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.dwak.redditslackbot.slack.model.SlackMessagePayload
import io.dwak.redditslackbot.slack.model.WebHookPayload
import io.dwak.redditslackbot.slack.model.WebHookPayloadAction
import io.dwak.redditslackbot.slack.model.WebHookPayloadAttachment
import io.dwak.redditslackbot.slack.model.isSpamRemoval
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RedditBotImpl @Inject constructor(private val service: RedditService,
                                        private val loginService: RedditLoginService,
                                        private val slackBot: SlackBot,
                                        private val dbHelper: DbHelper,
                                        @AppConfig private val appConfig: Map<String, String>,
                                        @RedditConfig private val redditConfig: Map<String, String>)
  : RedditBot {

  companion object {
    val POST_WINDOW = 5L
  }

  private val hostUrl by lazy { appConfig[ConfigValues.Application.HOST_URL] }
  private val hostPath by lazy { appConfig[ConfigValues.Application.HOST_PATH] }
  private val clientId by lazy { redditConfig[ConfigValues.Reddit.CLIENT_ID]!! }
  private val clientSecret by lazy { redditConfig[ConfigValues.Reddit.CLIENT_SECRET]!! }
  private val basicAuth by lazy {
    "Basic ${Base64.getEncoder().encodeToString(("$clientId:$clientSecret").toByteArray())}"
  }

  private val pollDisposables = hashMapOf<String, Disposable>()
  private val inProgressLogins = hashMapOf<String, String>()

  override fun beginLogin(state: String, path: String) {
    inProgressLogins.put(state, path)
  }

  override fun login(state: String, code: String): Single<Pair<String, RedditInfo>> {
    return loginService.getAccessToken(basicAuth, "authorization_code", code, "$hostUrl$hostPath/reddit-login")
        .map {
          RedditInfo.builder()
              .accessToken(it.accessToken)
              .refreshToken(it.refreshToken)
              .expiresIn(it.expiresIn)
              .lastTokenRefresh(Instant.now())
              .scope(it.scope)
              .tokenType(it.tokenType)
              .build()
        }
        .map { inProgressLogins[state]!! to it }
        .doOnSuccess {
          dbHelper.saveRedditInfo(it.first, it.second)
          inProgressLogins.remove(state)
        }
  }

  override fun saveSubreddit(path: String, subreddit: String): Completable {
    return dbHelper.getRedditInfo(path)
        .map { it.withSubreddit(subreddit) }
        .map { dbHelper.saveRedditInfo(path, it) }
        .doOnSuccess {
          pollForPosts(path)
          dbHelper.setLastCheckedTime(path, ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(POST_WINDOW))
          slackBot.postToChannel(path, WebHookPayload("Use /redditbot add rule "))
        }
        .toCompletable()
  }

  override fun pollForPosts(path: String) {
    pollDisposables[path]?.dispose()
    pollDisposables.put(path,
        Observable.interval(0, 5L, TimeUnit.MINUTES)
            .flatMapSingle {
              Single.zip(dbHelper.getSlackInfo(path), dbHelper.getRedditInfo(path).flatMap { refreshTokenIfNeeded(path) },
                  PairUtil.createPair<SlackInfo, RedditInfo>())
            }
            .subscribe { (_, redditInfo) ->
              Observable.just(redditInfo)
                  .flatMapSingle { service.unmoderated(redditInfo.bearerAccessToken(), redditInfo.subreddit()!!) }
                  .map { it.data }
                  .flatMap { Observable.fromArray(*it.children) }
                  .map { it.data }
                  .flatMapSingle { data -> dbHelper.getLastCheckedTime(path).map { it to data } }
                  .filter {
                    val (lastCheckedTime, data) = it
                    val createdUtc = ZonedDateTime.of(LocalDateTime.ofEpochSecond(data.created_utc, 0, ZoneOffset.UTC),
                        ZoneOffset.UTC)
                    createdUtc.isAfter(lastCheckedTime)
                  }
                  .map { it.second }
                  .flatMapSingle { data -> dbHelper.getPostedIds(path).map { it to data } }
                  .filter { !it.first.contains(it.second.id) }
                  .map { it.second }
                  .doOnNext { dbHelper.putPostedId(path, it.id).subscribe() }
                  .map {
                    val postBody = if (it.isSelfPost()) it.selftext else it.url

                    var title = "*Title*: ${it.title}"
                    if (it.isSuspiciousPost()) {
                      title = "*SUSPICIOUS POST*\n $title"
                    }
                    WebHookPayload(title,
                        listOf(
                            WebHookPayloadAttachment(
                                "Author: <https://www.reddit.com/u/${it.author}|${it.author}>" +
                                    "\n<https://www.reddit.com${it.permalink}|Post Link>" +
                                    "\n URL: ${it.url ?: "self-post"}" +
                                    "\nID: ${it.id}" +
                                    "\nPost Body: $postBody",
                                "can't remove",
                                it.id,
                                "default",
                                listOf(WebHookPayloadAction(ButtonAction.ACTION_BEGIN_REMOVE.value,
                                    "Remove",
                                    "button",
                                    ButtonAction.ACTION_BEGIN_REMOVE.value),
                                    WebHookPayloadAction(ButtonAction.ACTION_FLAIR.value,
                                        "Flair",
                                        "button",
                                        ButtonAction.ACTION_FLAIR.value)
                                )
                            )
                        )
                    )
                  }
                  .flatMapCompletable {
                    slackBot.postToChannel(path, payload = it)
                        .andThen { dbHelper.setLastCheckedTime(path, ZonedDateTime.now(ZoneOffset.UTC)) }
                  }
                  .subscribe({ },
                      { t -> slackBot.postToChannel(path, WebHookPayload(t.message ?: "Error in poll!")).subscribe() })
            })
  }

  override fun selectRemovalReason(path: String, payload: SlackMessagePayload): Completable {
    return dbHelper.getCannedResponses(path)
        .map { it to payload }
        .map { (rules, p) ->
          val originalMessage = p.originalMessage
          val originalAttachment = originalMessage.attachments!![0]
          val newActionsList = arrayListOf<WebHookPayloadAction>()
          rules.forEach {
            newActionsList.add(WebHookPayloadAction(it.title, it.title,
                value = "${ButtonAction.ACTION_REMOVAL.value}_${it.id}"))
          }
          val newMessage = originalMessage.copy(attachments =
          listOf(originalAttachment.copy(actions = newActionsList)))
          p.responseUrl to newMessage
        }
        .flatMapCompletable { slackBot.updateMessage(it.first, it.second) }
  }

  override fun removePost(path: String, p: SlackMessagePayload): Completable {
    return Single.zip(dbHelper.getRedditInfo(path).flatMap { refreshTokenIfNeeded(path) },
        dbHelper.getCannedResponse(path, p.actions[0].value.removePrefix("${ButtonAction.ACTION_REMOVAL.value}_")),
        PairUtil.createPair<RedditInfo, CannedResponse>())
        .map { it.toTriple(p) }
        .flatMap { (redditInfo, response, payload) ->
          val responseWithFooter = response.copy(
              message = response.message +
                  "\n Beep. Boop. I am not human and will not respond to messages. " +
                  "Please file all complaints to " +
                  "[/r/${redditInfo.subreddit()}](https://www.reddit.com/r/${redditInfo.subreddit()}) " +
                  "via [modmail](https://www.reddit.com/message/compose?to=%2Fr%2F${redditInfo.subreddit()})")
          val fullName = "t3_${payload.callbackId}"
          val isSpam = payload.isSpamRemoval()
          val removePost = service.removePost(redditInfo.bearerAccessToken(), fullName, isSpam)

          if (isSpam) {
            removePost.toSingle { responseWithFooter to payload }
          }
          else {
            removePost.toSingle { "" }
                .flatMap {
                  service.postComment(redditInfo.bearerAccessToken(), thingId = fullName,
                      text = responseWithFooter.message)
                }
                .flatMapCompletable {
                  service.distinguish(redditInfo.bearerAccessToken(), id = it.json.data.things[0].data.name)
                }
                .toSingle { responseWithFooter to payload }
          }
        }
        .map { (response, payload) ->
          val originalMessage = payload.originalMessage
          val newMessage = originalMessage.copy(attachments = listOf(
              WebHookPayloadAttachment(text = "\nRemoved by ${payload.user.name} for ${response.title}!"
                  + "\n${originalMessage.attachments!![0].text}",
                  fallback = "Removed!",
                  callback_id = payload.callbackId,
                  actions = emptyList())))
          Pair(payload.responseUrl, newMessage)
        }
        .flatMapCompletable { slackBot.updateMessage(it.first, it.second) }
  }

  override fun beginFlair(path: String, payload: SlackMessagePayload): Completable {
    return dbHelper.getRedditInfo(path)
        .flatMap { refreshTokenIfNeeded(path) }
        .flatMap {
          service.flairSelector(it.bearerAccessToken(),
              it.subreddit()!!,
              "t3_${payload.callbackId}")
        }
        .map { (choices) ->
          val originalMessage = payload.originalMessage
          val originalAttachment = originalMessage.attachments!![0]
          val newActionsList = arrayListOf<WebHookPayloadAction>()
          choices
              .forEach {
                newActionsList.add(WebHookPayloadAction(name = it.flairTemplateId,
                    text = it.flairText,
                    value = ButtonAction.ACTION_SELECT_FLAIR.value))
              }
          return@map originalMessage.copy(attachments = listOf(originalAttachment.copy(actions = newActionsList)))
        }
        .flatMapCompletable { slackBot.updateMessage(payload.responseUrl, it) }
  }


  override fun selectFlair(path: String, payload: SlackMessagePayload): Completable {
    return dbHelper.getRedditInfo(path)
        .flatMap { refreshTokenIfNeeded(path) }
        .flatMapCompletable {
          service.selectFlair(authorization = it.bearerAccessToken(),
              subreddit = it.subreddit()!!,
              flairTemplateId = payload.actions[0].name,
              fullname = "t3_${payload.callbackId}")
        }
        .doFinally {
          val originalMessage = payload.originalMessage
          val copy = originalMessage.copy(attachments = listOf(
              WebHookPayloadAttachment(text = originalMessage.attachments!![0].text +
                  "\nFlaired by ${payload.user.name}!",
                  fallback = "Flaired!",
                  callback_id = payload.callbackId,
                  actions = emptyList())))
          slackBot.updateMessage(payload.responseUrl, copy).subscribe()
        }
  }

  private fun refreshTokenIfNeeded(path: String): Single<RedditInfo> {
    return dbHelper.getRedditInfo(path)
        .flatMap { info ->
          if (info.lastTokenRefresh().toEpochMilli() + info.expiresIn() < Instant.now().toEpochMilli()) {
            loginService.getRefreshToken(basicAuth, "refresh_token", info.refreshToken())
                .map {
                  info.toBuilder()
                      .accessToken(it.accessToken)
                      .tokenType(it.tokenType)
                      .expiresIn(it.expiresIn)
                      .lastTokenRefresh(Instant.now())
                      .scope(it.scope)
                      .build()
                }
                .doOnSuccess { dbHelper.saveRedditInfo(path, it) }
          }
          else {
            Single.just(info)
          }
        }
  }
}