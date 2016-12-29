package io.dwak.redditslackbot.reddit

import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.reddit.network.service.RedditLoginService
import io.dwak.redditslackbot.reddit.network.service.RedditService
import io.reactivex.Single
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedditBot @Inject constructor(private val service: RedditService,
                                    private val loginService: RedditLoginService,
                                    private val dbHelper: DbHelper) {
  private var basicAuth: String? = null

  fun login(subreddit: String,
            username: String,
            password: String,
            clientId: String,
            clientSecret: String,
            path: String): Single<RedditInfo> {
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
  }
}