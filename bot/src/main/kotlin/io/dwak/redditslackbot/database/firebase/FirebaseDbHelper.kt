package io.dwak.redditslackbot.database.firebase

import com.google.common.hash.Hashing
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.reddit.model.CannedResponse
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.reactivex.Completable
import io.reactivex.Single
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirebaseDbHelper @Inject constructor(private val firebaseDatabase: FirebaseDatabase) : DbHelper {

  companion object {
    private const val DB_CANNED_RESPONSES = "cannedResponses"
    private const val DB_SLACK = "slack"
    private const val DB_REDDIT = "reddit"
    private const val DB_POSTED_IDS = "postedIds"
    private const val DB_LAST_CHECKED_TIME = "lastCheckedTime"
  }

  override fun saveSlackInfo(info: SlackInfo) {
    val refRoot = hashForRefRoot("${info.teamId()}-${info.channelId()}")
    firebaseDatabase.getReference(refRoot)
        .child(DB_SLACK)
        .setValue(info.toFirebaseValue())
  }

  override fun getSlackInfo(path: String): Single<SlackInfo> {
    return Single.create<SlackInfo> { emitter ->
      firebaseDatabase.getReference(hashForRefRoot(path))
          .child(DB_SLACK)
          .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = emitter.onError(p0?.toException())
            override fun onDataChange(p0: DataSnapshot) = emitter.onSuccess(SlackInfo.create(p0))
          })
    }
  }

  override fun saveRedditInfo(path: String, info: RedditInfo) {
    val refRoot = hashForRefRoot(path)
    firebaseDatabase.getReference(refRoot)
        .child(DB_REDDIT)
        .setValue(info.toFirebaseValue())
  }

  override fun getRedditInfo(path: String): Single<RedditInfo> {
    return Single.create<RedditInfo> { emitter ->
      firebaseDatabase.getReference(hashForRefRoot(path))
          .child(DB_REDDIT)
          .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = emitter.onError(p0?.toException())
            override fun onDataChange(p0: DataSnapshot) = emitter.onSuccess(RedditInfo.create(p0))
          })
    }
  }

  override fun getAllEntries(): Single<Set<Pair<SlackInfo, RedditInfo>>> {
    return Single.create { emitter ->
      firebaseDatabase.reference.addValueEventListener(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) = emitter.onError(p0?.toException())

        override fun onDataChange(p0: DataSnapshot) {
          emitter.onSuccess(p0.children
              .map { SlackInfo.create(p0.child(DB_SLACK)) to RedditInfo.create(p0.child(DB_REDDIT)) }
              .toSet())
        }
      })
    }
  }

  private fun hashForRefRoot(path: String) = Hashing.sha256()
      .hashString(path, StandardCharsets.UTF_8)
      .toString()


  override fun getPostedIds(path: String): Single<Set<String>> {
    val refRoot = hashForRefRoot(path)
    return Single.create { emitter ->
      firebaseDatabase.getReference(refRoot)
          .child(DB_POSTED_IDS)
          .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = emitter.onError(p0?.toException())

            override fun onDataChange(p0: DataSnapshot) = emitter.onSuccess(p0.children
                .map { it.getValue(String::class.java) }.toSet())
          })
    }
  }

  override fun putPostedId(path: String, id: String): Completable {
    val refRoot = hashForRefRoot(path)
    return getPostedIds(path)
        .flatMapCompletable { postedIds ->
          val newSet = postedIds + id
          Completable.create { emitter ->
            firebaseDatabase.getReference(refRoot)
                .child(DB_POSTED_IDS)
                .setValue(newSet.toList().takeLast(10))
          }
        }
  }

  override fun getLastCheckedTime(path: String): Single<ZonedDateTime> {
    return Single.create { emitter ->
      firebaseDatabase.getReference(hashForRefRoot(path))
          .child(DB_LAST_CHECKED_TIME)
          .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = emitter.onError(p0?.toException())

            override fun onDataChange(p0: DataSnapshot?) {
              p0?.let {
                emitter.onSuccess(if (it.exists()) {
                  ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.getValue(Long::class.java)), ZoneOffset.UTC)
                }
                else {
                  ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(5)
                })
              }
            }
          })
    }
  }

  override fun setLastCheckedTime(path: String, time: ZonedDateTime): Completable {
    firebaseDatabase.getReference(hashForRefRoot(path))
        .child(DB_LAST_CHECKED_TIME)
        .setValue(time.toInstant().toEpochMilli())
    return Completable.complete()
  }


  override fun addCannedResponse(path: String, rule: CannedResponse): Completable {
    firebaseDatabase.getReference(hashForRefRoot(path))
        .child(DB_CANNED_RESPONSES)
        .child(rule.id)
        .setValue(mapOf(rule.title to rule.message))
    return Completable.complete()
  }

  override fun getCannedResponses(path: String): Single<List<CannedResponse>> {
    return Single.create { emitter ->
      firebaseDatabase.getReference(hashForRefRoot(path))
          .child(DB_CANNED_RESPONSES)
          .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = emitter.onError(p0?.toException())

            override fun onDataChange(p0: DataSnapshot?) {
              emitter.onSuccess(p0?.children
                  ?.map {
                    val key = it.key
                    val ruleMap = it.getValue(object : GenericTypeIndicator<HashMap<String, String>>() {})
                    val mutableEntry = ruleMap.entries.toList()[0]
                    CannedResponse(key, mutableEntry.key, mutableEntry.value)
                  })
            }
          })
    }
  }

  override fun getCannedResponse(path: String, key: String): Single<CannedResponse> {
    return Single.create { emitter ->
      firebaseDatabase.getReference(hashForRefRoot(path))
          .child(DB_CANNED_RESPONSES)
          .child(key)
          .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = emitter.onError(p0?.toException())

            override fun onDataChange(p0: DataSnapshot?) {
              val ruleEntry = p0?.getValue(object : GenericTypeIndicator<HashMap<String, String>>() {})
                  ?.entries
                  ?.toList()
                  ?.get(0)
              emitter.onSuccess(CannedResponse(key, ruleEntry?.key!!, ruleEntry?.value!!))
            }
          })
    }
  }

  override fun removeRule(path: String, id: String): Completable {
    return Completable.create { emitter ->
      firebaseDatabase.getReference(hashForRefRoot(path))
          .child(DB_CANNED_RESPONSES)
          .child(id)
          .removeValue { databaseError, databaseReference ->
            when (databaseError) {
              null -> emitter.onComplete()
              else -> emitter.onError(databaseError.toException())
            }
          }
    }
  }
}