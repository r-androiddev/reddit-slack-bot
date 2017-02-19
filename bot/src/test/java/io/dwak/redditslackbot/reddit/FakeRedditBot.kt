package io.dwak.redditslackbot.reddit

import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.slack.model.SlackMessagePayload
import io.reactivex.Completable
import io.reactivex.Single


open class FakeRedditBot : RedditBot {
  override fun login(state: String, code: String): Single<Pair<String, RedditInfo>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun saveSubreddit(path: String, subreddit: String): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun pollForPosts(path: String) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun selectRemovalReason(path: String, payload: SlackMessagePayload): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun removePost(path: String, payload: SlackMessagePayload): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun flairPost(path: String, payload: SlackMessagePayload): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun selectFlair(path: String, payload: SlackMessagePayload): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}