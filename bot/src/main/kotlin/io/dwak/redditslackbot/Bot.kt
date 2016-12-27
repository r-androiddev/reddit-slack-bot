package io.dwak.redditslackbot

import com.spotify.apollo.AppInit
import com.spotify.apollo.Environment
import com.spotify.apollo.RequestContext
import com.spotify.apollo.route.Route.async
import io.dwak.redditslackbot.reddit.RedditBot
import io.dwak.redditslackbot.slack.SlackBot
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class Bot @Inject constructor(val redditBot: RedditBot, val slackBot: SlackBot) : AppInit {
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
      registerAutoRoute(async("GET", "/check-posts", checkPosts()))
      registerAutoRoute(async("GET", "/init", slackLogin()))
      registerAutoRoute(async("POST", "/button", onButton()))
    }
  }

  private fun checkPosts(): (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      future.complete("Checking Posts!")
    }
  }

  private fun slackLogin(): (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      future.complete("Checking Posts!")
    }
  }

  private fun onButton(): (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      future.complete("Checking Posts!")
    }
  }

  fun completableFuture(rc: RequestContext,
                        f: (RequestContext, CompletableFuture<String>) -> Unit)
      : CompletableFuture<String> {
    return CompletableFuture<String>().apply {
      f(rc, this)
    }
//    f(rc, stage)
//    return stage
  }
}