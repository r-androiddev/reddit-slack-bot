package io.dwak.redditslackbot.inject.module.db

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import io.dwak.redditslackbot.inject.annotation.qualifier.FirebaseConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import java.io.FileInputStream
import javax.inject.Singleton

@Module
@Singleton
class FirebaseModule {
  @Provides @Singleton fun firebase(@FirebaseConfig config: Map<String, String>)

      : FirebaseDatabase {
    val classLoader = javaClass.classLoader
    val configFile = classLoader.getResource(config[ConfigValues.Firebase.ACCOUNT_KEY_PATH]).file
    val options = FirebaseOptions.Builder()
        .setDatabaseUrl("https://${config[ConfigValues.Firebase.PROJECT_ID]}.firebaseio.com/")
        .setServiceAccount(FileInputStream(configFile))
        .build()

    FirebaseApp.initializeApp(options)
    return FirebaseDatabase.getInstance()
  }
}