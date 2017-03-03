package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.label
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class Index @Inject constructor(): RequestAction{
  override val name: String = ""
  override val method: String = "GET"
  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { _, future ->
      future.complete(StringBuilder()
          .appendln("<!DOCTYPE html>")
          .appendHTML(false)
          .html {
            head {
              link(rel = "stylesheet", href = "http://fonts.googleapis.com/icon?family=Material+Icons")
              link(rel = "stylesheet", href = "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/css/materialize.min.css")
            }
            body {
              script(type = ScriptType.textJavaScript, src = "https://code.jquery.com/jquery-2.1.1.min.js")
              script(type = ScriptType.textJavaScript, src = "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/js/materialize.min.js")
              div("container") {
                label { +"hi" }
              }
            }
          }.toString()
      )
    }
  }
}