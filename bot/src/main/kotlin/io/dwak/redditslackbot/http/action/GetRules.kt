package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class GetRules @Inject constructor(val dbHelper: DbHelper) : RequestAction {
  override val name = "get-rules"
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
          dbHelper.getRules(path)
              .subscribe { ruleList, t ->
                future.complete(ruleList.toString())
              }
        }
      }

    }
  }
}