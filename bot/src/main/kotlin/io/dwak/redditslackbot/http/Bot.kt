package io.dwak.redditslackbot.http

import com.spotify.apollo.AppInit
import com.spotify.apollo.Environment
import com.spotify.apollo.route.Route.async
import io.dwak.redditslackbot.http.action.CheckPosts
import io.dwak.redditslackbot.http.action.OnButton
import io.dwak.redditslackbot.http.action.RedditLogin
import io.dwak.redditslackbot.http.action.SlackLogin
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject

class Bot @Inject constructor(private val slackLogin: SlackLogin,
                              private val redditLogin: RedditLogin,
                              private val checkPosts: CheckPosts,
                              private val onButton: OnButton)
  : AppInit {
  private var lastCheckedTime: ZonedDateTime

  companion object {
    const val POST_WINDOW = 5L
    const val CACHE_SIZE = 10
  }

  init {
    lastCheckedTime = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(POST_WINDOW)
  }

  override fun create(env: Environment) {
    with(env.routingEngine()) {
      registerAutoRoute(async("GET", "/init", slackLogin.action))
      registerAutoRoute(async("POST", "/init-reddit", redditLogin.action))
      registerAutoRoute(async("GET", "/check-posts", checkPosts.action))
      registerAutoRoute(async("POST", "/button", onButton.action))
    }
  }
}