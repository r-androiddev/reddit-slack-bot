package io.dwak.redditslackbot.reddit.model

import io.dwak.redditslackbot.reddit.model.SuspicionTag

enum class Kind(val kind : String) {
  LISTING("Listing"),
  T3("t3")
}

data class RedditListing(val kind : Kind,
                         val data : ListingData)

data class ListingData(val modhash : String,
                       val children : Array<ChildrenData>)

data class ChildrenData(val kind : Kind,
                        val data : T3Data)

data class T3Data(
        val domain : String?,
        val subreddit : String?,
        val selftext_html : String?,
        val selftext : String?,
        val id : String,
        val author : String,
        val score : Int,
        val permalink : String,
        val created : Long,
        val url : String?,
        val title : String,
        val created_utc : Long)

fun T3Data.isSelfPost() : Boolean {
  return selftext != null
}

fun T3Data.isSuspiciousPost() : Boolean {
  return SuspicionTag.values()
          .map { it.tag }
          .map { title.contains(it) }
          .take(1)[0]
}