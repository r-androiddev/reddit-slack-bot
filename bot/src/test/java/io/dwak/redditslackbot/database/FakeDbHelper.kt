package io.dwak.redditslackbot.database

import io.dwak.redditslackbot.reddit.model.CannedResponse
import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.reactivex.Completable
import io.reactivex.Single
import java.time.ZonedDateTime


open class FakeDbHelper: DbHelper {
  override fun saveSlackInfo(info: SlackInfo) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getSlackInfo(path: String): Single<SlackInfo> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun saveRedditInfo(path: String, info: RedditInfo) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getRedditInfo(path: String): Single<RedditInfo> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getAllEntries(): Single<Set<Pair<SlackInfo, RedditInfo>>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getPostedIds(path: String): Single<Set<String>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun putPostedId(path: String, id: String): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getLastCheckedTime(path: String): Single<ZonedDateTime> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun setLastCheckedTime(path: String, time: ZonedDateTime): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun addCannedResponse(path: String, rule: CannedResponse): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getCannedResponses(path: String): Single<List<CannedResponse>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getCannedResponse(path: String, key: String): Single<CannedResponse> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun removeRule(path: String, id: String): Completable {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
