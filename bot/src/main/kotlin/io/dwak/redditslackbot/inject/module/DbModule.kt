package io.dwak.redditslackbot.inject.module

import dagger.Binds
import dagger.Module
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.database.firebase.FirebaseDbHelper
import javax.inject.Singleton


@Module
@Singleton
abstract class DbModule {
  @Binds @Singleton abstract fun db(firebaseDbHelper: FirebaseDbHelper) : DbHelper
}