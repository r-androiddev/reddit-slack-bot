package io.dwak.redditslackbot.inject.module


import dagger.Module
import dagger.Provides
import io.dwak.redditslackbot.inject.annotation.qualifier.reddit.Reddit
import io.dwak.redditslackbot.reddit.network.service.RedditLoginService
import io.dwak.redditslackbot.reddit.network.service.RedditOauthInterceptor
import io.dwak.redditslackbot.reddit.network.service.RedditService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.*
import javax.inject.Singleton


@Module
class RedditModule {

  @Provides
  @Reddit(login = false)
  fun redditServiceBaseUrl() = "https://oauth.reddit.com/r/"

  @Provides
  @Reddit(login = true)
  fun loginServiceBaseUrl() = "https://www.reddit.com/"

  @Provides
  @Reddit(login = true)
  fun loginOkHttp(@Reddit(login = true) okHttpBuilder : OkHttpClient.Builder,
                  interceptors : ArrayList<Interceptor>)
      : OkHttpClient {
    okHttpBuilder.interceptors().addAll(interceptors)
    return okHttpBuilder.build()
  }

  @Provides
  @Reddit
  fun redditOkHttp(@Reddit okHttpBuilder : OkHttpClient.Builder,
                   interceptors : ArrayList<Interceptor>)
      : OkHttpClient {
    okHttpBuilder.interceptors().addAll(interceptors)
    return okHttpBuilder.build()
  }

  @Provides
  @Reddit(login = true)
  fun loginOkHttpBuilder() : OkHttpClient.Builder {
    return OkHttpClient.Builder()
  }

  @Provides
  @Reddit
  fun redditOkHttpBuilder(redditOauthInterceptor : RedditOauthInterceptor) : OkHttpClient.Builder {
    val okHttpBuilder = OkHttpClient.Builder()
    okHttpBuilder.addInterceptor(redditOauthInterceptor)
    return okHttpBuilder
  }

  @Provides
  @Singleton
  @Reddit
  fun redditServiceRetrofit(converterFactory : Converter.Factory,
                            callAdapterFactory : CallAdapter.Factory,
                            @Reddit client : OkHttpClient,
                            @Reddit baseUrl : String) : Retrofit
      = Retrofit.Builder()
      .addConverterFactory(converterFactory)
      .addCallAdapterFactory(callAdapterFactory)
      .client(client)
      .baseUrl(baseUrl)
      .build()

  @Provides
  @Singleton
  @Reddit(login = true)
  fun loginServiceRetrofit(converterFactory : Converter.Factory,
                           callAdapterFactory : CallAdapter.Factory,
                           @Reddit(login = true) client : OkHttpClient,
                           @Reddit(login = true) baseUrl : String) : Retrofit
      = Retrofit.Builder()
      .addConverterFactory(converterFactory)
      .addCallAdapterFactory(callAdapterFactory)
      .client(client)
      .baseUrl(baseUrl)
      .build()

  @Provides
  @Singleton
  fun redditService(@Reddit retrofit : Retrofit) = retrofit.create(RedditService::class.java)

  @Provides
  @Singleton
  fun loginService(@Reddit(login = true) retrofit : Retrofit) = retrofit.create(RedditLoginService::class.java)
}