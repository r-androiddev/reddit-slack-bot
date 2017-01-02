package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.inject.annotation.qualifier.reddit.RedditConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import io.dwak.redditslackbot.reddit.RedditBot
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
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class SlackLogin @Inject constructor(private val slackBot: SlackBot,
                                     private val redditBot: RedditBot,
                                     @RedditConfig private val redditConfig: Map<String, String>) : RequestAction {

  companion object {
    const val name = "slack-login"
  }

  private val clientId by lazy { redditConfig[ConfigValues.Reddit.CLIENT_ID]!! }

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

            val state = UUID.randomUUID()
                .toString()
                .apply { redditBot.beginLogin(this, "${slackInfo.teamId()}-${slackInfo.channelId()}") }

            future.complete(StringBuilder()
                .appendln("<!DOCTYPE html>")
                .appendHTML()
                .html {
                  head {
                    //                    link(rel = "stylesheet", href = "http://fonts.googleapis.com/icon?family=Material+Icons")
//                    link(rel = "stylesheet", href = "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/css/materialize.min.css")
                    meta(content = "0; url=https://www.reddit.com/api/v1/authorize?client_id=$clientId" +
                        "&response_type=code" +
                        "&state=$state" +
                        "&redirect_uri=https://37b15491.ngrok.io/init-reddit" +
                        "&duration=permanent" +
                        "&scope=identity edit flair history modconfig modflair modlog modposts modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread") {
                      httpEquiv = "refresh"
                    }
                  }
                }.toString())
          }
    }
  }
}