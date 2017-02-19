package io.dwak.redditslackbot.extension

import java.util.*

fun <T> Optional<T>.toKotlinOptional() : T? {
  if(this.isPresent){
    return this.get()
  }
  else {
    return null
  }
}


