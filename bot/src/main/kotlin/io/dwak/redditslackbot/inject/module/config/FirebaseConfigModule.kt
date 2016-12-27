package io.dwak.redditslackbot.inject.module.config

import com.typesafe.config.Config
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import io.dwak.redditslackbot.extension.getStringFor
import io.dwak.redditslackbot.inject.annotation.qualifier.FirebaseConfig
import javax.inject.Singleton

@Module
@Singleton
class FirebaseConfigModule {
  @Provides
  @IntoMap @StringKey(ConfigValues.Firebase.ACCOUNT_KEY_PATH)
  @Singleton @FirebaseConfig fun accountKeyPath(config: Config)
      = config.getStringFor(ConfigValues.Firebase, ConfigValues.Firebase.ACCOUNT_KEY_PATH)

  @Provides
  @IntoMap @StringKey(ConfigValues.Firebase.PROJECT_ID)
  @Singleton @FirebaseConfig fun projectId(config: Config)
      = config.getStringFor(ConfigValues.Firebase, ConfigValues.Firebase.PROJECT_ID)
}