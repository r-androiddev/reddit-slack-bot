package io.dwak.redditslackbot.database.firebase

import com.google.common.hash.Hashing
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.reactivex.Observable
import io.reactivex.Single
import sun.security.provider.MD5
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirebaseDbHelper @Inject constructor(private val firebaseDatabase: FirebaseDatabase) : DbHelper {
  override fun saveSlackInfo(info: SlackInfo) {
    val refRoot = hashForRefRoot("${info.teamId()}-${info.channelId()}")
    firebaseDatabase.getReference(refRoot)
        .child("slack")
        .setValue(info.toFirebaseValue())
  }

  override fun getSlackInfo(path: String): Single<SlackInfo> {
    return Single.create<SlackInfo> { emitter ->
      firebaseDatabase.getReference(hashForRefRoot(path))
          .child("slack")
          .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) = emitter.onError(p0?.toException())
            override fun onDataChange(p0: DataSnapshot) = emitter.onSuccess(SlackInfo.create(p0))
          })
    }
  }

  override fun saveRedditInfo(path: String, info: RedditInfo) {
    val refRoot = hashForRefRoot(path)
    firebaseDatabase.getReference(refRoot)
        .child("reddit")
        .setValue(info.toFirebaseValue())
  }

  override fun getRedditInfo(path: String): Single<RedditInfo> {
    return Single.create<RedditInfo> { emitter ->
      firebaseDatabase.getReference(hashForRefRoot(path))
          .child("reddit")
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
              .map { SlackInfo.create(p0.child("slack")) to RedditInfo.create(p0.child("reddit")) }
              .toSet())
        }
      })
    }
  }

  private fun hashForRefRoot(path: String) = Hashing.sha256()
      .hashString(path, StandardCharsets.UTF_8)
      .toString()
}