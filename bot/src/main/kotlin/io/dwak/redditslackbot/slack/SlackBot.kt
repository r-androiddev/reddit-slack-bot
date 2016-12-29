package io.dwak.redditslackbot.slack

import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.inject.annotation.qualifier.slack.SlackConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.dwak.redditslackbot.slack.network.SlackOauthService
import io.dwak.redditslackbot.slack.network.SlackService
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SlackBot @Inject constructor(private val slackService: SlackService,
                                   private val loginService: SlackOauthService,
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
}