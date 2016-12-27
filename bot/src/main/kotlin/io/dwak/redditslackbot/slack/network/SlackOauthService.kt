package io.dwak.redditslackbot.slack.network

import io.dwak.redditslackbot.slack.model.SlackOauthResponse
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface SlackOauthService {
  @FormUrlEncoded
  @POST("oauth.access")
  fun getOauth(@Field("client_id") clientId : String,
               @Field("client_secret") clientSecret : String,
               @Field("code") code : String)
      : Single<SlackOauthResponse>
}