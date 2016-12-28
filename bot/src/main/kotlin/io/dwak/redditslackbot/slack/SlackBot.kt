package io.dwak.redditslackbot.slack

import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.inject.annotation.qualifier.slack.SlackConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import io.dwak.redditslackbot.slack.network.SlackOauthService
import io.dwak.redditslackbot.slack.network.SlackService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SlackBot @Inject constructor(val slackService: SlackService,
                                   val loginService: SlackOauthService,
                                   val dbHelper: DbHelper,
                                   @SlackConfig val config: Map<String, String>) {
  fun login(code: String) {
    loginService.getOauth(config[ConfigValues.Slack.CLIENT_ID]!!,
        config[ConfigValues.Slack.CLIENT_SECRET]!!, code)
        .filter { it.accessToken == config[ConfigValues.Slack.VERIFICATION_TOKEN]!! }
  }
}