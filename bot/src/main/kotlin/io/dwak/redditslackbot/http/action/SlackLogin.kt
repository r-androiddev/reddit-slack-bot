package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.slack.SlackBot
import kotlinx.html.ButtonType
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.link
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class SlackLogin @Inject constructor(private val slackBot: SlackBot) : RequestAction {

  companion object { const val name = "slack-login" }

  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      val code = req.request().parameter("code").orElse("")
      if (code.isEmpty()) {
        future.complete("Error logging in!")
      }
      slackBot.login(code)
          .subscribe { slackInfo, throwable ->
            if (throwable != null) {
              future.complete("Something went wrong! ${throwable.message}")
            }
            future.complete(StringBuilder()
                .appendln("<!DOCTYPE html>")
                .appendHTML()
                .html {
                  head {
                    link(rel = "stylesheet", href = "http://fonts.googleapis.com/icon?family=Material+Icons")
                    link(rel = "stylesheet", href = "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/css/materialize.min.css")
                  }
                  body {
                    script(type = ScriptType.textJavaScript, src = "https://code.jquery.com/jquery-2.1.1.min.js")
                    script(type = ScriptType.textJavaScript, src = "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/js/materialize.min.js")
                    div("container") {
                      form(action = "init-reddit?data=${slackInfo.teamId()}-${slackInfo.channelId()}",
                          method = FormMethod.post) {
                        h3 { +"${slackInfo.teamName()}: ${slackInfo.channel()}" }
                        div("row") {
                          form(classes = "col s12") {
                            div("row") {
                              div("input-field col s12") {
                                input(InputType.text, name = "subreddit")
                                label {
                                  for_ = "subreddit"
                                  +"Subreddit (just the name, no \"/r/\")"
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
                }.toString())
          }
    }
  }
}