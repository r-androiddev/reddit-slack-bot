package io.dwak.redditslackbot.database

import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.reactivex.Observable
import io.reactivex.Single


interface DbHelper {
  fun saveSlackInfo(info: SlackInfo)

  fun getSlackInfo(path: String): Single<SlackInfo>

  fun saveRedditInfo(path: String, info: RedditInfo)

  fun getRedditInfo(path:String): Single<RedditInfo>

  fun getAllEntries() : Single<Set<Pair<SlackInfo, RedditInfo>>>
}