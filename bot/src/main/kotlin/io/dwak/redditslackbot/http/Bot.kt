package io.dwak.redditslackbot.http

import com.spotify.apollo.AppInit
import com.spotify.apollo.Environment
import com.spotify.apollo.route.Route.async
import io.dwak.redditslackbot.http.action.SlashCommand
import io.dwak.redditslackbot.http.action.FinalizeReddit
import io.dwak.redditslackbot.http.action.OnButton
import io.dwak.redditslackbot.http.action.RedditLogin
import io.dwak.redditslackbot.http.action.SlackLogin
import javax.inject.Inject

class Bot @Inject constructor(private val actions: Set<@JvmSuppressWildcards RequestAction>) : AppInit {

  override fun create(env: Environment) = actions.forEach {
    env.routingEngine().registerAutoRoute(async(it.method, "/${it.name}", it.action))
  }
}