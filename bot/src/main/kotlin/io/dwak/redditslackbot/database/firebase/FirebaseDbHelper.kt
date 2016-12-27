package io.dwak.redditslackbot.database.firebase

import com.google.firebase.database.FirebaseDatabase
import io.dwak.redditslackbot.database.DbHelper
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirebaseDbHelper @Inject constructor(val firebaseDatabase: FirebaseDatabase) : DbHelper {
}