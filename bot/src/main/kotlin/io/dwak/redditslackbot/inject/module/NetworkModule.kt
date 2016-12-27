package io.dwak.redditslackbot.inject.module

import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.*


@Module(includes = arrayOf(RedditModule::class, SlackModule::class))
class NetworkModule {

  @Provides
  fun interceptors(): ArrayList<Interceptor> {
    val interceptors = arrayListOf<okhttp3.Interceptor>()
    val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println))
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    interceptors.add(loggingInterceptor)

    return interceptors
  }

  @Provides
  fun adapterFactory(): CallAdapter.Factory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
}