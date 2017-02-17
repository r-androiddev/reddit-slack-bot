package io.dwak.redditslackbot.reddit.model


data class CannedResponse(val id: String, val title: String, val message: String)

fun CannedResponse.messageWithFooter(subreddit: String)
    = message + "\n\nBeep. Boop. I am not human and will not respond to messages. Please file all complaints to" +
    " /r/$subreddit via" +
    " [modmail](https://www.reddit.com/message/compose?to=%2Fr%2F$subreddit)"
