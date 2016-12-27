package io.dwak.redditslackbot.reddit.network.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import io.dwak.redditslackbot.reddit.model.Kind

@Suppress("unused")
class KindAdapter {
  @ToJson fun toJson(kind : Kind) = kind.kind
  @FromJson fun fromJson(kind : String) = Kind.valueOf(kind.toUpperCase())
}