package io.dwak.redditslackbot.http.action

import com.google.common.truth.Truth.assertThat
import com.spotify.apollo.Request
import com.spotify.apollo.test.ServiceHelper
import io.dwak.redditslackbot.TestBot
import io.dwak.redditslackbot.database.FakeDbHelper
import io.dwak.redditslackbot.extension.createRequestContext
import io.dwak.redditslackbot.reddit.model.CannedResponse
import io.reactivex.Completable
import okio.ByteString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AddCannedResponseTest {

  private val dbHelper = object : FakeDbHelper() {
    override fun addCannedResponse(path: String, rule: CannedResponse) = Completable.complete()
  }

  private val requestAction = AddCannedResponse(dbHelper)
  @get:Rule val s = ServiceHelper.create(TestBot(requestAction), "test")
  private val stubClient = s.stubClient()

  @Test
  fun testAddSuccess() {
    val cannedResponse = "\"id\" \"title\" \"message\""
    val req = stubClient.createRequestContext(Request.forUri("http://test/${requestAction.name}", "POST")
        .withPayload(ByteString.encodeUtf8("team_id=valid&channel_id=path&text=$cannedResponse")))

    val res = requestAction.action.invoke(req).get()
    assertThat(res).contains("Saving")
  }

  @Test
  fun testAddFail() {
    val cannedResponse = "\"id\" \"title\""
    val req = stubClient.createRequestContext(Request.forUri("http://test/${requestAction.name}", "POST")
        .withPayload(ByteString.encodeUtf8("team_id=valid&channel_id=path&text=$cannedResponse")))

    val res = requestAction.action.invoke(req).get()
    assertThat(res).contains("Something went wrong")
  }

}