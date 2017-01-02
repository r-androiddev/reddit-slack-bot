package io.dwak.redditslackbot.reddit.network.service

import io.dwak.redditslackbot.reddit.model.AuthData
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface RedditLoginService {

  @FormUrlEncoded
  @POST("/api/v1/access_token")
  fun getAccessToken(@Header("Authorization") authorization: String,
                     @Field("grant_type") grantType: String,
                     @Field("code") code: String,
                     @Field("redirect_uri") redirectUri: String)
      : Single<AuthData>

  @FormUrlEncoded
  @POST("/api/v1/access_token")
  fun getRefreshToken(@Header("Authorization") authorization: String,
                     @Field("grant_type") grantType: String,
                     @Field("refresh_token") refreshToken: String)
      : Single<AuthData>
}