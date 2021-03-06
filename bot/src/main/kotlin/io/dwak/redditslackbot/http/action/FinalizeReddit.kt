package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.extension.toKotlinOptional
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.reddit.RedditBot
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class FinalizeReddit @Inject constructor(private val redditBot: RedditBot) : RequestAction {

  override val name = "finalize-reddit"
  override val method = "POST"

  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      val map = req.request().payload().map { it.payloadToMap() }
      val path = req.request().parameter("data").toKotlinOptional()
      if (!map.isPresent || path == null) {
        future.complete("Something went wrong!")
      }
      else {
        map.ifPresent {
          redditBot.saveSubreddit(path, it["subreddit"]!!)
              .subscribe {
                future.complete("All Done!")
              }
        }
      }
    }
  }
}