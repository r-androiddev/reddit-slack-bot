package io.dwak.redditslackbot.http.action

import com.google.common.truth.Truth.assertThat
import com.spotify.apollo.Request
import com.spotify.apollo.test.ServiceHelper
import io.dwak.redditslackbot.TestBot
import io.dwak.redditslackbot.database.FakeDbHelper
import io.dwak.redditslackbot.extension.createRequestContext
import io.reactivex.Completable
import okio.ByteString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class RemoveCannedResponseTest {
  companion object {
    const val DATA = "valid-path"
    const val CANNED_RESPONSE_ID = "id"
    const val TEST_SUBREDDIT = "testsubreddit"
  }

  private val dbHelper by lazy {
    object: FakeDbHelper() {
      override fun removeCannedResponse(path: String, id: String): Completable {
        assertThat(path).isEqualTo(DATA)
        assertThat(id).isEqualTo(CANNED_RESPONSE_ID)
        return Completable.complete()
      }
    }
  }

  private val requestAction = RemoveCannedResponse(dbHelper)
  @get:Rule val s = ServiceHelper.create(TestBot(requestAction), "test")
  private val stubClient = s.stubClient()

  @Test
  fun testRemoveSuccess() {
    val req = stubClient.createRequestContext(Request.forUri("http://test/${requestAction.name}", "POST")
        .withPayload(ByteString.encodeUtf8("team_id=valid&channel_id=path&text=$CANNED_RESPONSE_ID")))

    val res = requestAction.action.invoke(req).get()

    assertThat(res).contains("Removed")
  }
}