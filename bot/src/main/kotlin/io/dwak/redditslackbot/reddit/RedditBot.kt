package io.dwak.redditslackbot.reddit

import com.squareup.moshi.Moshi
import io.dwak.redditslackbot.database.DbHelper
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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
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
                                    private val moshi: Moshi,
                                    private val dbHelper: DbHelper) {

  companion object {
    val ACTION_FLAIR = "flair"
    val ACTION_REMOVE = "remove"
    val POST_WINDOW = 5L
    val CACHE_SIZE = 10
    val ACTION_SELECT_FLAIR = "select-flair"
  }

  private var basicAuth: String? = null

  private val postedIds: LinkedHashMap<String, T3Data>

  private var lastCheckedTime: ZonedDateTime

  private val pollDisposables = hashMapOf<String, Disposable>()

  init {
    lastCheckedTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(POST_WINDOW)

    postedIds = object : LinkedHashMap<String, T3Data>(CACHE_SIZE) {
      override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, T3Data>?): Boolean {
        return size >= CACHE_SIZE
      }
    }
  }

  fun login(subreddit: String, username: String, password: String,
            clientId: String, clientSecret: String, path: String): Single<RedditInfo> {
    basicAuth = "Basic ${Base64.getEncoder()
        .encodeToString(("$clientId:$clientSecret")
            .toByteArray())}"
    return loginService.getAccessToken(authorization = basicAuth!!,
        username = username,
        password = password)
        .map {
          RedditInfo.builder()
              .subreddit(subreddit)
              .accessToken(it.accessToken)
              .botUsername(username)
              .expiresIn(it.expiresIn)
              .scope(it.scope)
              .tokenType(it.tokenType)
              .build()
        }
        .doOnSuccess { dbHelper.saveRedditInfo(path, it) }
        .doOnSuccess { pollForPosts(path) }
  }

  fun pollForPosts(path: String) {
    pollDisposables.put(path,
        Single.zip(dbHelper.getSlackInfo(path), dbHelper.getRedditInfo(path),
            BiFunction<SlackInfo, RedditInfo, Pair<SlackInfo, RedditInfo>> { s, r -> s to r })
            .subscribe { infos, throwable ->
              Observable.interval(0, 5L, TimeUnit.MINUTES)
                  .flatMapSingle { service.unmoderated("bearer ${infos.second.accessToken()}", infos.second.subreddit()) }
                  .map { it.data }
                  .flatMap { Observable.fromArray(*it.children) }
                  .map { it.data }
                  .filter {
                    val createdUtc = ZonedDateTime.of(LocalDateTime.ofEpochSecond(it.created_utc, 0, ZoneOffset.UTC),
                        ZoneOffset.UTC)
                    createdUtc.isAfter(lastCheckedTime)
                  }
                  .filter { !postedIds.containsKey(it.id) }
                  .doOnNext { postedIds.put(it.id, it) }
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
                    lastCheckedTime = ZonedDateTime.now(ZoneOffset.UTC)
                  }
            })
  }

}