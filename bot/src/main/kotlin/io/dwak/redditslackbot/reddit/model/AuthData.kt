package io.dwak.redditslackbot.reddit.model

import com.squareup.moshi.Json

data class AuthData(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "expires_in") val expiresIn: Long,
    val scope: String,
    @Json(name = "token_type") val tokenType: String)

fun AuthData.isValid() {

}
