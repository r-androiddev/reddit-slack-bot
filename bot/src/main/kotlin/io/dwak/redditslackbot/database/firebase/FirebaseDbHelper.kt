package io.dwak.redditslackbot.database.firebase

import com.google.common.hash.Hashing
import com.google.firebase.database.FirebaseDatabase
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.slack.model.SlackInfo
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

  override fun saveRedditInfo(path: String, info: RedditInfo) {
    val refRoot = hashForRefRoot(path)
    firebaseDatabase.getReference(refRoot)
        .child("reddit")
        .setValue(info.toFirebaseValue())
  }

  private fun hashForRefRoot(path: String) = Hashing.sha256()
      .hashString(path, StandardCharsets.UTF_8)
      .toString()
}