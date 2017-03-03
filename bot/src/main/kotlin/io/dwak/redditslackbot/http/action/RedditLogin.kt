package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.reddit.RedditBot
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class RedditLogin @Inject constructor(private val redditBot: RedditBot) : RequestAction {

  override val name = "reddit-login"
  override val method = "GET"

  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      val state = req.request().parameter("state").get()
      val code = req.request().parameter("code").get()
      redditBot.login(state, code)
          .subscribe { pathRedditInfoPair, throwable ->
            val (path, redditInfo) = pathRedditInfoPair
            if (throwable != null) {
              future.complete("Something went wrong! ${throwable.message}")
            }
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
                      form(action = "finalize-reddit?data=$path",
                          method = FormMethod.post) {
                        div("row") {
                          form(classes = "col s12") {
                            div("row") {
                              div("input-field col s12") {
                                input(InputType.text, name = "subreddit") { required = true }
                                label {
                                  for_ = "subreddit"
                                  +"Subreddit (just the name, no \"/r/\")"
                                }
                              }
                              div("input-field col s12") {
                                button(type = ButtonType.submit, classes = "btn waves-effect waves-light") {
                                  name = "action"
                                  +"Submit"
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
                .toString())
          }
    }
  }
}