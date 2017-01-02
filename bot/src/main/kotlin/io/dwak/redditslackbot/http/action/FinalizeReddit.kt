package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.reddit.RedditBot
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class FinalizeReddit @Inject constructor(private val redditBot: RedditBot): RequestAction {
  companion object {
    const val name = "finalize-reddit"
  }
  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      val map = req.request().payload().map { it.payloadToMap() }
      if(!map.isPresent){
        future.complete("Something went wrong!")
      }
      else {
        map.ifPresent {
          val path = req.request().parameter("data").get()
          redditBot.saveSubreddit(path, it["subreddit"]!!)
              .subscribe {
                future.complete("All Done!")
              }
        }
      }
    }
  }
}