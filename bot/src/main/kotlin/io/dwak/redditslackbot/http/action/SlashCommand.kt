package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import dagger.Lazy
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.extension.payloadToMap
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.reddit.RedditBot
import io.dwak.redditslackbot.slack.SlackBot
import io.dwak.redditslackbot.slack.model.WebHookPayload
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class SlashCommand @Inject constructor(redditBotLazy: Lazy<RedditBot>,
                                       slackBotLazy: Lazy<SlackBot>,
                                       dbHelperLazy: Lazy<DbHelper>)
  : RequestAction {

  override val name = "slash-command"
  override val method = "POST"

  private val redditBot by lazy { redditBotLazy.get() }
  private val slackBot by lazy { slackBotLazy.get() }
  private val dbHelper by lazy { dbHelperLazy.get() }

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
          when (params["text"]) {
            "check" -> responseMessage = check(path)
            "info" -> responseMessage = info(path)
            else -> responseMessage = "Sorry, can't handle that"
          }
        }
      }
      future.complete(responseMessage)
    }
  }

  fun check(path: String): String {
    redditBot.pollForPosts(path)
    return "Checking Posts!"
  }

  fun info(path: String): String {
    dbHelper.getSlackInfo(path)
        .flatMap { slackInfo ->
          dbHelper.getRedditInfo(path)
              .map { slackInfo to it }
        }
        .map { slackRedditPair ->
          val (slackInfo, redditInfo) = slackRedditPair
          WebHookPayload("Bot info:\n" +
              "*Slack*\n" +
              "Channel: ${slackInfo.channel()}\n" +
              "Team: ${slackInfo.teamName()}\n" +
              "*Reddit*\n" +
              "Subreddit: ${redditInfo.subreddit()}")
        }
        .flatMapCompletable { slackBot.postToChannel(path, it) }
        .subscribe()
    return "Gathering bits"
  }
}