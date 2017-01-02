package io.dwak.redditslackbot.database.firebase

import me.mattlogan.auto.value.firebase.adapter.TypeAdapter
import java.time.Instant

class InstantAdapter : TypeAdapter<Instant, Long> {
  override fun fromFirebaseValue(value: Long): Instant = Instant.ofEpochMilli(value)
  override fun toFirebaseValue(value: Instant): Long = value.toEpochMilli()
}