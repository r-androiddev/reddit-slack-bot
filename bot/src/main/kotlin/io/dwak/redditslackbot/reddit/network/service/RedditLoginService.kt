package io.dwak.redditslackbot.reddit.network.service

import io.dwak.redditslackbot.reddit.model.AuthData
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface RedditLoginService {
  @FormUrlEncoded
  @POST("/api/v1/access_token")
  fun getAccessToken(@Header("Authorization") authorization : String,
                     @Header("User-Agent") userAgent : String = "RedditBot/0.1 by DWAK",
                     @Field("grant_type") grantType : String = "password",
                     @Field("refresh_token") refreshToken : String? = null,
                     @Field("duration") duration : String = "permanent",
                     @Field("username") username : String,
                     @Field("password") password : String)
          : Call<AuthData>
}