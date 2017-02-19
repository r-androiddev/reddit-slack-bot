package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.inject.annotation.qualifier.AppConfig
import io.dwak.redditslackbot.inject.annotation.qualifier.reddit.RedditConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import io.dwak.redditslackbot.reddit.RedditBotImpl
import io.dwak.redditslackbot.slack.SlackBot
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.meta
import kotlinx.html.stream.appendHTML
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class SlackLogin @Inject constructor(private val slackBot: SlackBot,
                                     private val redditBot: RedditBotImpl,
                                     @AppConfig private val appConfig: Map<String, String>,
                                     @RedditConfig private val redditConfig: Map<String, String>) : RequestAction {

  override val name = "slack-login"
  override val method = "GET"

  private val clientId by lazy { redditConfig[ConfigValues.Reddit.CLIENT_ID]!! }
  private val hostUrl by lazy { appConfig[ConfigValues.Application.HOST_URL] }
  private val redditLoginScopes = listOf("identity", "edit", "flair", "history", "modconfig",
      "modflair", "modlog", "modposts", "modwiki", "mysubreddits",
      "privatemessages", "read", "report", "save", "submit",
      "subscribe", "vote", "wikiedit", "wikiread")

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
                    var scopeString = ""
                    redditLoginScopes.take(redditLoginScopes.size - 1)
                        .map { it + " " }
                        .forEach { scopeString += it }
                    scopeString += redditLoginScopes.last()
                    meta(content = "0; url=https://www.reddit.com/api/v1/authorize?client_id=$clientId" +
                        "&response_type=code" +
                        "&state=$state" +
                        "&redirect_uri=$hostUrl/reddit-login" +
                        "&duration=permanent" +
                        "&scope=$scopeString") {
                      httpEquiv = "refresh"
                    }
                  }
                }.toString())
          }
    }
  }
}