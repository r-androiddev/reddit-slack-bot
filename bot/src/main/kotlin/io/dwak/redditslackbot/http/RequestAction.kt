package io.dwak.redditslackbot.http

import com.spotify.apollo.RequestContext
import java.util.concurrent.CompletableFuture


interface RequestAction {
  val action: (RequestContext) -> CompletableFuture<String>
}

fun completableFuture(rc: RequestContext, f: (RequestContext, CompletableFuture<String>) -> Unit)
    = CompletableFuture<String>().apply { f(rc, this) }
