package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class GetCannedResponses @Inject constructor(private val dbHelper: DbHelper) : RequestAction {
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
              .subscribe { ruleList, _ ->
                future.complete(ruleList.toString())
              }
        }
      }

    }
  }
}