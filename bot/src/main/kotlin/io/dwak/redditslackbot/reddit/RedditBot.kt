package io.dwak.redditslackbot.reddit

import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.http.action.RedditLogin
import io.dwak.redditslackbot.inject.annotation.qualifier.AppConfig
import io.dwak.redditslackbot.inject.annotation.qualifier.reddit.RedditConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.reddit.model.T3Data
import io.dwak.redditslackbot.reddit.model.isSelfPost
import io.dwak.redditslackbot.reddit.model.isSuspiciousPost
import io.dwak.redditslackbot.reddit.network.service.RedditLoginService
import io.dwak.redditslackbot.reddit.network.service.RedditService
import io.dwak.redditslackbot.slack.SlackBot
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.dwak.redditslackbot.slack.model.WebHookPayload
import io.dwak.redditslackbot.slack.model.WebHookPayloadAction
import io.dwak.redditslackbot.slack.model.WebHookPayloadAttachment
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedditBot @Inject constructor(private val service: RedditService,
                                    private val loginService: RedditLoginService,
                                    private val slackBot: SlackBot,
                                    private val dbHelper: DbHelper,
                                    @AppConfig private val appConfig: Map<String, String>,
                                    @RedditConfig private val redditConfig: Map<String, String>) {

  companion object {
    val ACTION_FLAIR = "flair"
    val ACTION_REMOVE = "remove"
    val POST_WINDOW = 5L
    val CACHE_SIZE = 10
    val ACTION_SELECT_FLAIR = "select-flair"
  }

  private val clientId by lazy { redditConfig[ConfigValues.Reddit.CLIENT_ID]!! }
  private val clientSecret by lazy { redditConfig[ConfigValues.Reddit.CLIENT_SECRET]!! }
  private val basicAuth by lazy {
    "Basic ${Base64.getEncoder().encodeToString(("$clientId:$clientSecret").toByteArray())}"
  }
  private val hostUrl by lazy { appConfig[ConfigValues.Application.HOST_URL] }

  private val postedIds = hashMapOf<String, LinkedHashMap<String, T3Data>>()
  private var lastCheckedTimes = hashMapOf<String, ZonedDateTime>()
  private val pollDisposables = hashMapOf<String, Disposable>()
  private val inProgressLogins = hashMapOf<String, String>()

  fun beginLogin(state: String, path: String) {
    inProgressLogins.put(state, path)
  }

  fun login(state: String, code: String): Single<Pair<String, RedditInfo>> {
    return loginService.getAccessToken(basicAuth, "authorization_code", code, "$hostUrl/reddit-login")
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

  fun saveSubreddit(path: String, subreddit: String): Completable {
    return dbHelper.getRedditInfo(path)
        .map { it.withSubreddit(subreddit) }
        .map { dbHelper.saveRedditInfo(path, it) }
        .doOnSuccess {
          postedIds[path] = object : LinkedHashMap<String, T3Data>(CACHE_SIZE) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, T3Data>?): Boolean {
              return size >= CACHE_SIZE
            }
          }
          pollForPosts(path)
          lastCheckedTimes.put(path, ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(POST_WINDOW))
        }
        .toCompletable()
  }

  fun pollForPosts(path: String) {
    pollDisposables.put(path,
        Single.zip(dbHelper.getSlackInfo(path), dbHelper.getRedditInfo(path).flatMap { refreshTokenIfNeeded(path) },
            BiFunction<SlackInfo, RedditInfo, Pair<SlackInfo, RedditInfo>> { s, r -> s to r })
            .subscribe { infos, throwable ->
              val (slackInfo, redditInfo) = infos
              Observable.interval(0, 5L, TimeUnit.MINUTES)
                  .flatMapSingle { service.unmoderated("bearer ${redditInfo.accessToken()}", redditInfo.subreddit()!!) }
                  .map { it.data }
                  .flatMap { Observable.fromArray(*it.children) }
                  .map { it.data }
                  .filter {
                    var lastCheckedTime = lastCheckedTimes[path]
                    val createdUtc = ZonedDateTime.of(LocalDateTime.ofEpochSecond(it.created_utc, 0, ZoneOffset.UTC),
                        ZoneOffset.UTC)
                    if (lastCheckedTime == null) {
                      lastCheckedTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(POST_WINDOW)
                      lastCheckedTimes.put(path, lastCheckedTime)
                    }
                    createdUtc.isAfter(lastCheckedTime)
                  }
                  .filter { postedIds[path]?.containsKey(it.id) ?: true }
                  .doOnNext { postedIds[path]?.put(it.id, it) }
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
                                    "\nID: ${it.id}" +
                                    "\nPost Body: $postBody",
                                "can't remove",
                                it.id,
                                "default",
                                listOf(WebHookPayloadAction(ACTION_REMOVE,
                                    "Remove",
                                    "button",
                                    ACTION_REMOVE),
                                    WebHookPayloadAction(ACTION_FLAIR,
                                        "Flair",
                                        "button",
                                        ACTION_FLAIR)
                                )
                            )
                        )
                    )
                  }
                  .flatMapCompletable { slackBot.postToChannel(path, payload = it) }
                  .subscribe {
                    lastCheckedTimes[path] = ZonedDateTime.now(ZoneOffset.UTC)
                  }
            })
  }

  fun removePost() {

  }

  fun flairPost() {

  }

}