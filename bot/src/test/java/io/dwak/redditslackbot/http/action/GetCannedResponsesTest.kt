package io.dwak.redditslackbot.http.action

import com.google.common.truth.Truth
import com.spotify.apollo.Request
import com.spotify.apollo.test.ServiceHelper
import io.dwak.redditslackbot.TestBot
import io.dwak.redditslackbot.database.FakeDbHelper
import io.dwak.redditslackbot.extension.createRequestContext
import io.dwak.redditslackbot.reddit.model.CannedResponse
import io.reactivex.Single
import okio.ByteString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class GetCannedResponsesTest {
  private val dbHelper = object : FakeDbHelper() {
    val cannedResponseMap = mapOf(
        "valid-path" to listOf(
            CannedResponse("response1", "title1", "message1"),
            CannedResponse("response2", "title2", "message2"))
    )

    override fun getCannedResponses(path: String): Single<List<CannedResponse>> {
      return Single.just(cannedResponseMap[path])
    }
  }

  private val requestAction = GetCannedResponses(dbHelper)
  @get:Rule val serviceHelper = ServiceHelper.create(TestBot(requestAction), "test")
  private val stubClient = serviceHelper.stubClient()

  @Test
  fun testResponseSuccess() {

    val req = stubClient.createRequestContext(Request.forUri("http://test/${requestAction.name}", "GET")
        .withPayload(ByteString.encodeUtf8("team_id=valid&channel_id=path")))

    val response = requestAction.action.invoke(req).get()
    Truth.assertThat(response).contains("response1")
    Truth.assertThat(response).contains("title1")
    Truth.assertThat(response).contains("message1")

    Truth.assertThat(response).contains("response2")
    Truth.assertThat(response).contains("title2")
    Truth.assertThat(response).contains("message2")
  }
}

