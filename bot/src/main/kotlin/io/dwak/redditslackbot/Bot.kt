package io.dwak.redditslackbot

import com.spotify.apollo.AppInit
import com.spotify.apollo.Environment
import com.spotify.apollo.RequestContext
import com.spotify.apollo.route.Route.async
import io.dwak.redditslackbot.reddit.RedditBot
import io.dwak.redditslackbot.slack.SlackBot
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.button
import kotlinx.html.buttonInput
import kotlinx.html.div
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.form
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.i
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import org.w3c.dom.Document
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
      registerAutoRoute(async("GET", "/init", slackLogin()))
      registerAutoRoute(async("POST", "/init-reddit", redditLogin()))
      registerAutoRoute(async("GET", "/check-posts", checkPosts()))
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
      val html = StringBuilder()
          .appendln("<!DOCTYPE html>")
          .appendHTML(true)
          .html {
            head {
              link(rel = "stylesheet", href = "http://fonts.googleapis.com/icon?family=Material+Icons")
              link(rel = "stylesheet", href = "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/css/materialize.min.css")
            }
            body {
              script(type = ScriptType.textJavaScript, src = "https://code.jquery.com/jquery-2.1.1.min.js")
              script(type = ScriptType.textJavaScript, src = "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/js/materialize.min.js")
              form(action = "init-reddit", method = FormMethod.post) {
                div("row") {
                  form(classes = "col s12") {
                    div("row") {
                      div("input-field col s12") {
                        input(InputType.text, name = "subreddit")
                        label {
                          for_ = "subreddit"
                          +"Subreddit"
                        }
                      }
                      div("input-field col s6") {
                        input(InputType.text, name = "bot_username")
                        label {
                          for_ = "bot_username"
                          +"Bot Username"
                        }
                      }
                      div("input-field col s6") {
                        input(InputType.text, name = "bot_password")
                        label {
                          for_ = "bot_password"
                          +"Bot Password"
                        }
                      }
                      div("input-field col s6") {
                        input(InputType.text, name = "client_id")
                        label {
                          for_ = "client_id"
                          +"Client Id"
                        }
                      }
                      div("input-field col s6") {
                        input(InputType.text, name = "client_secret")
                        label {
                          for_ = "client_secret"
                          +"Client Secret"
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
      future.complete(html.toString())
    }
  }

  private fun redditLogin(): (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      future.complete("reddit: ${req.request().payload().get().utf8()}")
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