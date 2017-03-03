package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.slack.SlackBot
import io.dwak.redditslackbot.slack.model.WebHookPayload
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class GetCannedResponses @Inject constructor(private val dbHelper: DbHelper, private val slackBot: SlackBot)
  : RequestAction {
  override val name = "get-canned-responses"
  override val method = "POST"
  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      val map = req.request().payload().map { it.payloadToMap() }
      if (!map.isPresent) {
        future.complete("Something went wrong!")
      }
      else {
        map.ifPresent { params: Map<String, String> ->
          val path = "${params["team_id"]}-${params["channel_id"]}"
          dbHelper.getCannedResponses(path)
              .map { responses ->
                responses.map {
                  "\n```" +
                      "\nID: ${it.id}" +
                      "\nTitle: ${it.title}" +
                      "\nMessage: ${it.message}" +
                      "\n```"
                }.joinToString(separator = "\n", prefix = "*Canned Responses*\n")
              }
              .flatMapCompletable { slackBot.postToChannel(path, WebHookPayload(it)) }
              .subscribe()
          future.complete("Gathering bits!")
        }
      }

    }
  }
}