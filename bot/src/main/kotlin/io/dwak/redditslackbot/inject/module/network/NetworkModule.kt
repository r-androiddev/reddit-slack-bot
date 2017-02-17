package io.dwak.redditslackbot.inject.module.network

import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.Logger
import retrofit2.CallAdapter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.*


@Module(includes = arrayOf(RedditModule::class, SlackModule::class))
class NetworkModule {

  @Provides
  fun interceptors(logger: Logger): ArrayList<Interceptor> {
    val interceptors = arrayListOf<Interceptor>()
    val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger({ logger.info(it) }))
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    interceptors.add(loggingInterceptor)

    return interceptors
  }

  @Provides
  fun adapterFactory(): CallAdapter.Factory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
}