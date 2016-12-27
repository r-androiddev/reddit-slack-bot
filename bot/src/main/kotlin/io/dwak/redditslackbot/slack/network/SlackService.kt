package io.dwak.redditslackbot.slack.network

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path


interface SlackService {
  @FormUrlEncoded
  @POST("/services/{id1}/{id2}/{id3}")
  fun postToWebHook(@Path("id1") id1 : String,
                    @Path("id2") id2 : String,
                    @Path("id3") id3 : String,
                    @Field("payload") payload : String)
      : Single<String>

  @FormUrlEncoded
  @POST("/actions/{id1}/{id2}/{id3}")
  fun respondToMessage(@Path("id1") id1 : String,
                       @Path("id2") id2 : String,
                       @Path("id3") id3 : String,
                       @Field("payload") payload : String)
      : Completable
}