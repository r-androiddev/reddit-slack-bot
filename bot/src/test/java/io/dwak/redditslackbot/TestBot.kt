package io.dwak.redditslackbot

import com.spotify.apollo.AppInit
import com.spotify.apollo.Environment
import com.spotify.apollo.route.Route
import io.dwak.redditslackbot.http.RequestAction

class TestBot(private val requestAction: RequestAction) : AppInit {
  override fun create(environment: Environment) {
    environment.routingEngine()
        .registerAutoRoute(Route.async(requestAction.method, "/${requestAction.name}", requestAction.action))
  }
}