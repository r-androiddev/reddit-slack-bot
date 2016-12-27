package io.dwak.redditslackbot.inject.module

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.dwak.redditslackbot.inject.module.config.ConfigModule
import io.dwak.redditslackbot.reddit.network.adapter.KindAdapter
import retrofit2.Converter
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Singleton
@Module(includes = arrayOf(
    ConfigModule::class,
    DbModule::class,
    FirebaseModule::class,
    NetworkModule::class))
class MainModule {
  @Provides
  @Singleton
  fun moshi(): Moshi = Moshi.Builder()
      .add(KindAdapter())
      .build()

  @Provides
  @Singleton
  fun moshiFactory(moshi: Moshi): Converter.Factory
      = MoshiConverterFactory.create(moshi).asLenient()

}