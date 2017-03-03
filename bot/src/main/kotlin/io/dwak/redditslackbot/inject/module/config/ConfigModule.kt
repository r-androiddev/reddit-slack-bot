package io.dwak.redditslackbot.inject.module.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import io.dwak.redditslackbot.extension.getStringFor
import io.dwak.redditslackbot.inject.annotation.qualifier.AppConfig
import javax.inject.Singleton


@Singleton
@Module(includes = arrayOf(
    FirebaseConfigModule::class,
    RedditConfigModule::class,
    SlackConfigModule::class))
class ConfigModule {
  @Provides @Singleton fun config(): Config = ConfigFactory.load()

  @Provides
  @IntoMap @StringKey(ConfigValues.Application.HOST_URL)
  @Singleton @AppConfig
  fun hostUrl(config: Config)
      = config.getStringFor(ConfigValues.Application, ConfigValues.Application.HOST_URL)

  @Provides
  @IntoMap @StringKey(ConfigValues.Application.HOST_PATH)
  @Singleton @AppConfig
  fun hostPath(config: Config)
      = config.getStringFor(ConfigValues.Application, ConfigValues.Application.HOST_PATH)
}