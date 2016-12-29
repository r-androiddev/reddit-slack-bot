package io.dwak.redditslackbot.extension

import okio.ByteString

fun ByteString.payloadToMap(): Map<String, String> {
  val map = hashMapOf<String, String>()
  val payloadString = this.utf8()
  payloadString.split("&")
      .map { it.split("=")[0] to it.split("=")[1] }
      .forEach { map.put(it.first, it.second) }
  return map
}


