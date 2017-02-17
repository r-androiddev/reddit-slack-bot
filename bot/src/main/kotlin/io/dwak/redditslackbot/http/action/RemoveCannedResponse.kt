package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class RemoveCannedResponse @Inject constructor(dbHelper: DbHelper) : RequestAction {
  override val name = "remove-canned-response"
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
          val id = params["text"]
          if (id != null) {
            dbHelper.removeRule(path, id).subscribe { future.complete("Removed $id") }
          }
          else {
            future.complete("id not found!")
          }
        }
      }
    }
  }
}