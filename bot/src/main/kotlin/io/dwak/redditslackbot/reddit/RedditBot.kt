package io.dwak.redditslackbot.reddit

import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.slack.model.SlackMessagePayload
import io.reactivex.Completable
import io.reactivex.Single


interface RedditBot {
  fun login(state: String, code: String): Single<Pair<String, RedditInfo>>

  fun saveSubreddit(path: String, subreddit: String): Completable

  fun pollForPosts(path: String)

  fun selectRemovalReason(path: String, payload: SlackMessagePayload): Completable

  fun removePost(path: String, payload: SlackMessagePayload): Completable

  fun flairPost(path: String, payload: SlackMessagePayload): Completable

  fun selectFlair(path: String, payload: SlackMessagePayload): Completable
}