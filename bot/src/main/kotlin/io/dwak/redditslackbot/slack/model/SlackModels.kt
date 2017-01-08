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


data class WebHookPayload(val text : String,
                          val attachments : List<WebHookPayloadAttachment>? = null)

data class WebHookPayloadAttachment(val text : String,
                                    val fallback : String,
                                    val callback_id : String,
                                    val attachment_type : String? = "default",
                                    val actions : List<WebHookPayloadAction>)

data class WebHookPayloadAction(val name : String,
                                val text : String,
                                val type : String = "button",
                                val value : String)


data class SlackMessagePayload(val actions : List<SlackMessagePayloadAction>,
                               @Json(name = "callback_id") val callbackId : String,
                               val token : String,
                               @Json(name = "original_message") val originalMessage : WebHookPayload,
                               @Json(name = "response_url") val responseUrl : String,
                               val user : SlackMessagePayloadUser)

data class SlackMessagePayloadAction(val name : String,
                                     val value  : String)

data class SlackMessagePayloadUser(val id : String, val name : String)