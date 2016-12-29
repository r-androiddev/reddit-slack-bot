package io.dwak.redditslackbot.inject.module.network

import dagger.Module
import dagger.Provides
import io.dwak.redditslackbot.inject.annotation.qualifier.slack.Slack
import io.dwak.redditslackbot.slack.network.SlackOauthService
import io.dwak.redditslackbot.slack.network.SlackService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.*
import javax.inject.Singleton


@Module
class SlackModule {
  @Provides
  @Slack
  fun slackServiceBaseUrl() = "https://hooks.slack.com/"

  @Provides
  @Slack(oauth = true)
  fun slackOauthServiceBaseUrl() = "https://slack.com/api/"

  @Provides
  @Slack
  fun slackOkHttpBuilder() : OkHttpClient.Builder {
    return OkHttpClient.Builder()
  }

  @Provides
  @Slack(oauth = true)
  fun slackOauthOkHttpBuilder() : OkHttpClient.Builder {
    return OkHttpClient.Builder()
  }

  @Provides
  @Slack
  fun slackOkHttp(@Slack okHttpBuilder : OkHttpClient.Builder,
                  interceptors : ArrayList<Interceptor>)
      : OkHttpClient {
    okHttpBuilder.interceptors().addAll(interceptors)
    return okHttpBuilder.build()
  }

  @Provides
  @Slack(oauth = true)
  fun slackOauthOkHttp(@Slack(oauth = true) okHttpBuilder : OkHttpClient.Builder,
                       interceptors : ArrayList<Interceptor>)
      : OkHttpClient {
    okHttpBuilder.interceptors().addAll(interceptors)
    return okHttpBuilder.build()
  }

  @Provides
  @Singleton
  @Slack
  fun slackServiceRetrofit(converterFactory : Converter.Factory,
                           callAdapterFactory : CallAdapter.Factory,
                           @Slack client : OkHttpClient,
                           @Slack baseUrl : String) : Retrofit
      = Retrofit.Builder()
      .addConverterFactory(converterFactory)
      .addCallAdapterFactory(callAdapterFactory)
      .client(client)
      .baseUrl(baseUrl)
      .build()

  @Provides
  @Singleton
  @Slack(oauth = true)
  fun slackOauthServiceRetrofit(converterFactory : Converter.Factory,
                                callAdapterFactory : CallAdapter.Factory,
                                @Slack(oauth = true) client : OkHttpClient,
                                @Slack(oauth = true) baseUrl : String) : Retrofit
      = Retrofit.Builder()
      .addConverterFactory(converterFactory)
      .addCallAdapterFactory(callAdapterFactory)
      .client(client)
      .baseUrl(baseUrl)
      .build()


  @Provides
  @Singleton
  fun slackServices(@Slack retrofit : Retrofit) = retrofit.create(SlackService::class.java)

  @Provides
  @Singleton
  fun slackOauthServices(@Slack(oauth = true) retrofit : Retrofit) = retrofit.create(SlackOauthService::class.java)
}