package io.dwak.redditslackbot.slack.model

import com.squareup.moshi.Json

data class SlackOauthResponse(val ok : Boolean,
                              @Json(name = "access_token") val accessToken : String,
                              val scope : String,
                              @Json(name = "user_id") val userId : String,
                              @Json(name = "team_name") val teamName : String,
                              @Json(name = "team_id") val teamId : String,
                              @Json(name = "incoming_webhook") val incomingWebHook : SlackIncomingWebHook)

data class SlackIncomingWebHook(val channel : String,
                                @Json(name = "channel_id") val channelId : String,
                                @Json(name = "configuration_url") val configurationUrl : String,
                                val url : SlackWebhookUrlComponents)

data class SlackWebhookUrlComponents(val id1 : String, val id2 : String, val id3 : String)