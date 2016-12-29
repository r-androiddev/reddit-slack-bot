package io.dwak.redditslackbot.slack.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import io.dwak.redditslackbot.slack.model.SlackWebhookUrlComponents
import me.mattlogan.auto.value.firebase.adapter.TypeAdapter

@Suppress("unused")
class SlackWebhookUrlComponentAdapter : TypeAdapter<SlackWebhookUrlComponents, String> {
  override fun fromFirebaseValue(value: String) = fromString(value)
  override fun toFirebaseValue(value: SlackWebhookUrlComponents) = fromComponents(value)

  @FromJson fun fromJson(url: String) = fromString(url)
  @ToJson fun toJson(value: SlackWebhookUrlComponents) = fromComponents(value)

  private fun fromString(url: String): SlackWebhookUrlComponents {
    val splits = url.split("services")
    val ids = splits[1].split("/")
    return SlackWebhookUrlComponents(ids[1], ids[2], ids[3])
  }

  private fun fromComponents(value: SlackWebhookUrlComponents): String {
    return "https://hooks.slack.com/services/${value.id1}/${value.id2}/${value.id3}"
  }
}

