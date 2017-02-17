package io.dwak.redditslackbot.database

import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.reddit.model.CannedResponse
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.reactivex.Completable
import io.reactivex.Single
import java.time.ZonedDateTime


interface DbHelper {
  fun saveSlackInfo(info: SlackInfo)

  fun getSlackInfo(path: String): Single<SlackInfo>

  fun saveRedditInfo(path: String, info: RedditInfo)

  fun getRedditInfo(path: String): Single<RedditInfo>

  fun getAllEntries(): Single<Set<Pair<SlackInfo, RedditInfo>>>

  fun getPostedIds(path: String): Single<Set<String>>

  fun putPostedId(path: String, id: String): Completable

  fun getLastCheckedTime(path: String): Single<ZonedDateTime>

  fun setLastCheckedTime(path: String, time: ZonedDateTime): Completable

  fun addCannedResponse(path: String, rule: CannedResponse): Completable

  fun getCannedResponses(path: String): Single<List<CannedResponse>>

  fun getCannedResponse(path: String, key: String): Single<CannedResponse>

  fun removeRule(path: String, id: String): Completable
}