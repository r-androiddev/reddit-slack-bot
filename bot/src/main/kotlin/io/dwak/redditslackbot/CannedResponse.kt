package io.dwak.redditslackbot

import javax.inject.Inject

data class Response(val displayName : String, val message : String)

class CannedResponses @Inject constructor() {
  val responses = hashMapOf<String, Response>()

  init {
    with(responses) {
      put("questions_thread",
          Response("Questions Thread",
                   "Removed because, per sub rules, this doesn't merit its own post. " +
                   "We have a questions thread every day, please use it for questions like this."))
      put("rules",
          Response("Rules",
                   "Removed because posts like this are against the sub rules."))
      put("wiki",
          Response("Wiki",
                   "Removed because relevant information can be found in the /r/androiddev " +
                   "[wiki](https://www.reddit.com/r/androiddev/wiki/index)"))
      put("spam",
          Response("Spam",
                   "Removed as spam"))
    }
  }
}

