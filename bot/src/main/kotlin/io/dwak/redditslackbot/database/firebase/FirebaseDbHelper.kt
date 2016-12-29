package io.dwak.redditslackbot.database.firebase

import com.google.firebase.database.FirebaseDatabase
import io.dwak.redditslackbot.database.DbHelper
import io.dwak.redditslackbot.reddit.model.RedditInfo
import io.dwak.redditslackbot.slack.model.SlackInfo
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirebaseDbHelper @Inject constructor(private val firebaseDatabase: FirebaseDatabase) : DbHelper {
  override fun saveSlackInfo(info: SlackInfo) {
    firebaseDatabase.getReference("${info.teamId()}-${info.channelId()}")
        .child("slack")
        .setValue(info.toFirebaseValue())
  }

  override fun saveRedditInfo(path: String, info: RedditInfo) {
    firebaseDatabase.getReference(path)
        .child("reddit")
        .setValue(info.toFirebaseValue())
  }

}