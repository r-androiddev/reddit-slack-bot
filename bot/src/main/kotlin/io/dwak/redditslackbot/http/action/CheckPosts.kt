package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class CheckPosts @Inject constructor() : RequestAction {
  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      future.complete("Checking Posts!")
    }
  }
}