package io.dwak.redditslackbot.slack

import com.squareup.moshi.Moshi
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.inject.annotation.qualifier.slack.SlackConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.dwak.redditslackbot.slack.model.WebHookPayload
import io.dwak.redditslackbot.slack.network.SlackOauthService
import io.dwak.redditslackbot.slack.network.SlackService
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SlackBot @Inject constructor(private val slackService: SlackService,
                                   private val loginService: SlackOauthService,
                                   private val moshi: Moshi,
                                   private val dbHelper: DbHelper,
                                   private @SlackConfig val config: Map<String, String>) {
  fun login(code: String): Single<SlackInfo> =
      loginService.getOauth(config[ConfigValues.Slack.CLIENT_ID]!!,
          config[ConfigValues.Slack.CLIENT_SECRET]!!, code)
          .map {
            SlackInfo.builder()
                .teamName(it.teamName)
                .teamId(it.teamId)
                .channel(it.incomingWebHook.channel)
                .channelId(it.incomingWebHook.channelId)
                .webHookUrl(it.incomingWebHook.url)
                .accessToken(it.accessToken)
                .build()
          }
          .doOnSuccess { dbHelper.saveSlackInfo(it) }

  fun postToChannel(path: String, payload: WebHookPayload): Completable {
    return dbHelper.getSlackInfo(path)
        .map { it to moshi.adapter(WebHookPayload::class.java).toJson(payload) }
        .flatMap {
          slackService.postToWebHook(it.first.webHookUrl().id1,
              it.first.webHookUrl().id2,
              it.first.webHookUrl().id3,
              it.second)
        }
        .toCompletable()
  }
}