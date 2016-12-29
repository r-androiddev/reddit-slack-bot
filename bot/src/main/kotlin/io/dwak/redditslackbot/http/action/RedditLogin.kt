package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.reddit.RedditBot
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class RedditLogin @Inject constructor(private val redditBot: RedditBot) : RequestAction {

  companion object {
    const val name = "reddit-login"
  }

  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      val map = req.request().payload().map { it.payloadToMap() }
      if (!map.isPresent) {
        future.complete("Something went wrong!")
      }
      else {
        map.ifPresent {
          redditBot.login(it["subreddit"]!!,
              it["bot_username"]!!,
              it["bot_password"]!!,
              it["client_id"]!!,
              it["client_secret"]!!,
              req.request().parameter("data").get())
              .subscribe { redditInfo, throwable ->
                if (throwable != null) {
                  future.complete("Something went wrong! ${throwable.message}")
                }
                future.complete("Alright!")
              }
        }
      }
    }
  }
}