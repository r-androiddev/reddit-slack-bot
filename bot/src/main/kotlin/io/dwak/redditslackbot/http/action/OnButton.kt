package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.reddit.RedditBot
import io.dwak.redditslackbot.slack.SlackBot
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class OnButton @Inject constructor(private val redditBot: RedditBot)
  : RequestAction {

  companion object {
    const val name = "on-button"
  }

  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      future.complete("Checking Posts!")
    }
  }

}