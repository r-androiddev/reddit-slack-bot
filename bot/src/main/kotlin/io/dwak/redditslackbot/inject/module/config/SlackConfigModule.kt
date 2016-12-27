package io.dwak.redditslackbot.inject.module.config

import com.typesafe.config.Config
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import io.dwak.redditslackbot.extension.getStringFor
import io.dwak.redditslackbot.inject.annotation.qualifier.slack.SlackConfig
import javax.inject.Singleton


@Module
@Singleton
class SlackConfigModule {

  @Provides
  @IntoMap @StringKey(ConfigValues.Slack.CLIENT_ID)
  @Singleton @SlackConfig
  fun clientId(config: Config): String
      = config.getStringFor(ConfigValues.Slack, ConfigValues.Slack.CLIENT_ID)

  @Provides
  @IntoMap @StringKey(ConfigValues.Slack.CLIENT_SECRET)
  @Singleton @SlackConfig
  fun clientSecret(config: Config): String
      = config.getStringFor(ConfigValues.Slack, ConfigValues.Slack.CLIENT_SECRET)

  @Provides
  @IntoMap @StringKey(ConfigValues.Slack.VERIFICATION_TOKEN)
  @Singleton @SlackConfig
  fun verificationToken(config: Config): String
      = config.getStringFor(ConfigValues.Slack, ConfigValues.Slack.VERIFICATION_TOKEN)
}