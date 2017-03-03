package io.dwak.redditslackbot.reddit

import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.slack.model.SlackMessagePayload
import io.reactivex.Completable
import io.reactivex.Single

open class FakeRedditBot : RedditBot {
  override fun beginLogin(state: String, path: String): Unit = TODO("not implemented")
  override fun login(state: String, code: String): Single<Pair<String, RedditInfo>> = TODO("not implemented")
  override fun saveSubreddit(path: String, subreddit: String): Completable = TODO("not implemented")
  override fun pollForPosts(path: String): Unit = TODO("not implemented")
  override fun selectRemovalReason(path: String, payload: SlackMessagePayload): Completable = TODO("not implemented")
  override fun removePost(path: String, payload: SlackMessagePayload): Completable = TODO("not implemented")
  override fun beginFlair(path: String, payload: SlackMessagePayload): Completable = TODO("not implemented")
  override fun selectFlair(path: String, payload: SlackMessagePayload): Completable = TODO("not implemented")
}