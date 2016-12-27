package io.dwak.redditslackbot.inject.module.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Singleton
@Module(includes = arrayOf(
    FirebaseConfigModule::class,
    RedditConfigModule::class,
    SlackConfigModule::class))
class ConfigModule {
  @Provides @Singleton fun config(): Config = ConfigFactory.load()
}