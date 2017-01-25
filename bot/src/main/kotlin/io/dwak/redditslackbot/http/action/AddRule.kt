package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.reddit.model.Rule
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class AddRule @Inject constructor(val dbHelper: DbHelper) : RequestAction {
  override val name = "add-rule"
  override val method = "POST"

  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      val map = req.request().payload().map { it.payloadToMap() }
      var responseMessage = ""
      if (!map.isPresent) {
        responseMessage = "Something went wrong!"
      }
      else {
        map.ifPresent { params: Map<String, String> ->
          val path = "${params["team_id"]}-${params["channel_id"]}"
          val ruleStringList = URLDecoder.decode(params["text"], "UTF-8")
              .split("\"")
              .filter(String::isNotEmpty)
              .filter(String::isNotBlank)

          if(ruleStringList.size != 3){
            responseMessage = "Something went wrong, check syntax!"
          }
          else {
            dbHelper.addRule(path, Rule(ruleStringList[0], ruleStringList[1], ruleStringList[2]))
            responseMessage = "Saving new rule!"
          }
        }
      }
      future.complete(responseMessage)
    }
  }

}


