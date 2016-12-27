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
  @IntoMap @StringKey(ConfigValues.Reddit.SUBREDDIT)
  @Singleton @RedditConfig
  fun subreddit(config: Config): String
      = config.getStringFor(ConfigValues.Reddit, ConfigValues.Reddit.SUBREDDIT)

  @Provides
  @IntoMap @StringKey(ConfigValues.Reddit.BOT_USERNAME)
  @Singleton @RedditConfig
  fun botUsername(config: Config): String
      = config.getStringFor(ConfigValues.Reddit, ConfigValues.Reddit.BOT_USERNAME)

  @Provides
  @IntoMap @StringKey(ConfigValues.Reddit.BOT_PASSWORD)
  @Singleton @RedditConfig
  fun botPassword(config: Config): String
      = config.getStringFor(ConfigValues.Reddit, ConfigValues.Reddit.BOT_PASSWORD)

  @Provides
  @IntoMap @StringKey(ConfigValues.Reddit.CLIENT_ID)
  @Singleton @RedditConfig
  fun clientId(config: Config): String
      = config.getStringFor(ConfigValues.Reddit, ConfigValues.Reddit.CLIENT_ID)

  @Provides
  @IntoMap @StringKey(ConfigValues.Reddit.CLIENT_SECRET)
  @Singleton @RedditConfig
  fun clientSecret(config: Config): String
      = config.getStringFor(ConfigValues.Reddit, ConfigValues.Reddit.CLIENT_SECRET)
}