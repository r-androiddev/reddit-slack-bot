package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.reddit.RedditBot
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class CheckPosts @Inject constructor(private val redditBot: RedditBot) : RequestAction {

  companion object {
    const val name = "check-posts"
  }

  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      val map = req.request().payload().map { it.payloadToMap() }
      if (!map.isPresent) {
        future.complete("Something went wrong!")
      }
      else {
        map?.ifPresent {
          when (it["text"]) {
            "check posts" -> redditBot.pollForPosts("${it["team_id"]}-${it["channel_id"]}")
          }
        }
      }
      future.complete("Checking Posts!")
    }
  }
}