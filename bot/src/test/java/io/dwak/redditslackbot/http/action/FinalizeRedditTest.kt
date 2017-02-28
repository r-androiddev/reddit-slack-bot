package io.dwak.redditslackbot.http.action

import com.google.common.truth.Truth.assertThat
import com.spotify.apollo.Request
import com.spotify.apollo.test.ServiceHelper
import io.dwak.redditslackbot.TestBot
import io.dwak.redditslackbot.extension.createRequestContext
import io.dwak.redditslackbot.reddit.FakeRedditBot
import io.reactivex.Completable
import okio.ByteString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FinalizeRedditTest {
  companion object {
    const val DATA = "valid-path"
    const val TEST_SUBREDDIT = "testsubreddit"
  }

  private val redditBot by lazy {
    object : FakeRedditBot() {
      override fun saveSubreddit(path: String, subreddit: String): Completable {
        assertThat(path).isEqualTo(DATA)
        assertThat(subreddit).isEqualTo(TEST_SUBREDDIT)
        return Completable.complete()
      }
    }
  }

  private val requestAction = FinalizeReddit(redditBot)
  @get:Rule val serviceHelper = ServiceHelper.create(TestBot(requestAction), "test")
  private val stubClient = serviceHelper.stubClient()

  @Test
  fun testSaveSubreddit() {
    val req = stubClient.createRequestContext(Request.forUri("http://test/${requestAction.name}?data=valid-path", "GET")
        .withPayload(ByteString.encodeUtf8("subreddit=$TEST_SUBREDDIT")))

    val res = requestAction.action.invoke(req).get()

    assertThat(res).isEqualTo("All Done!")
  }

  @Test
  fun testNoDataParam() {
    val req = stubClient.createRequestContext(Request.forUri("http://test/${requestAction.name}", "GET")
        .withPayload(ByteString.encodeUtf8("subreddit=$TEST_SUBREDDIT")))

    val res = requestAction.action.invoke(req).get()

    assertThat(res).isEqualTo("Something went wrong!")
  }
}