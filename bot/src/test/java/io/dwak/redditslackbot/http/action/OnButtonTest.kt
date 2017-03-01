package io.dwak.redditslackbot.http.action

import com.spotify.apollo.test.ServiceHelper
import io.dwak.redditslackbot.TestBot
import io.dwak.redditslackbot.inject.module.MainModule
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import io.dwak.redditslackbot.reddit.FakeRedditBot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OnButtonTest {
  companion object {
    const val VERIFICATION_TOKEN = "token"
  }
  private val redditBot  = object: FakeRedditBot() {

  }

  private val moshi = MainModule().moshi()

  private val slackConfig = mapOf(ConfigValues.Slack.VERIFICATION_TOKEN to VERIFICATION_TOKEN)

  private val requestAction = OnButton(redditBot, moshi, slackConfig)
  @get:Rule val serviceHelper = ServiceHelper.create(TestBot(requestAction), "test")
  private val stubClient = serviceHelper.stubClient()

  @Test
  fun testBeginRemove() {

  }
}