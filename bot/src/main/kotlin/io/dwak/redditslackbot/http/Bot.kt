package io.dwak.redditslackbot.http

import com.spotify.apollo.AppInit
import com.spotify.apollo.Environment
import com.spotify.apollo.route.Route.async
import io.dwak.redditslackbot.http.action.CheckPosts
import io.dwak.redditslackbot.http.action.OnButton
import io.dwak.redditslackbot.http.action.RedditLogin
import io.dwak.redditslackbot.http.action.SlackLogin
import javax.inject.Inject

class Bot @Inject constructor(private val actions: Map<String, @JvmSuppressWildcards RequestAction>)
  : AppInit {

  override fun create(env: Environment) {
    with(env.routingEngine()) {
      registerAutoRoute(async("GET", "/init", actions[SlackLogin.name]?.action))
      registerAutoRoute(async("POST", "/init-reddit", actions[RedditLogin.name]?.action))
      registerAutoRoute(async("GET", "/check-posts", actions[CheckPosts.name]?.action))
      registerAutoRoute(async("POST", "/button", actions[OnButton.name]?.action))
    }
  }
}