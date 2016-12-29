package io.dwak.redditslackbot.extension

import okio.ByteString

fun ByteString.payloadToMap(): Map<String, String> = this.utf8()
    .split("&")
    .map {
      val propValue = it.split("=")
      propValue[0] to propValue[1]
    }
    .toMap()


