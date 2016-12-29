package io.dwak.redditslackbot.database

import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.slack.model.SlackInfo


interface DbHelper {
  fun saveSlackInfo(info: SlackInfo)
  fun saveRedditInfo(path: String, info: RedditInfo)
}