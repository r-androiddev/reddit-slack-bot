package io.dwak.redditslackbot.reddit.model

data class RedditCommentResponse(val json : RedditCommentResponseChild)

data class RedditCommentResponseChild(val data : RedditCommentResponseData)

data class RedditCommentResponseData(val things : List<RedditCommentResponseDataThing>)

data class RedditCommentResponseDataThing(val kind : String,
                                          val data : RedditCommentResponseDataThingData)

data class RedditCommentResponseDataThingData(val id : String,
                                              val name : String)

