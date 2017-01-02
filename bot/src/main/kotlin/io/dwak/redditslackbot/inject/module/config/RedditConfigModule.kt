package io.dwak.redditslackbot.inject.module.config

import com.typesafe.config.Config
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import io.dwak.redditslackbot.extension.getStringFor
import io.dwak.redditslackbot.inject.annotation.qualifier.reddit.RedditConfig
import javax.inject.Singleton


@Module
@Singleton
class RedditConfigModule {
  @Provides
  @IntoMap @StringKey(ConfigValues.Slack.CLIENT_ID)
  @Singleton @RedditConfig
  fun clientId(config: Config): String
      = config.getStringFor(ConfigValues.Reddit, ConfigValues.Reddit.CLIENT_ID)

  @Provides
  @IntoMap @StringKey(ConfigValues.Slack.CLIENT_SECRET)
  @Singleton @RedditConfig
  fun clientSecret(config: Config): String
      = config.getStringFor(ConfigValues.Reddit, ConfigValues.Reddit.CLIENT_SECRET)
}