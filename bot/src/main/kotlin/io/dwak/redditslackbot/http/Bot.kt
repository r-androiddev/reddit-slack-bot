package io.dwak.redditslackbot.http

import com.spotify.apollo.AppInit
import com.spotify.apollo.Environment
import com.spotify.apollo.route.Route.async
import io.dwak.redditslackbot.inject.annotation.qualifier.AppConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import javax.inject.Inject

class Bot @Inject constructor(private val actions: Set<@JvmSuppressWildcards RequestAction>,
                              @AppConfig private val appConfig: Map<String, String>) : AppInit {

  override fun create(env: Environment) = actions.forEach {
    env.routingEngine()
        .registerAutoRoute(async(it.method,
            "${appConfig[ConfigValues.Application.HOST_PATH]}/${it.name}",
            it.action))
  }

}

