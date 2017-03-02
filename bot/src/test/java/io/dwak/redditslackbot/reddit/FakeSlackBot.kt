package io.dwak.redditslackbot.reddit

import io.dwak.redditslackbot.slack.SlackBot
import io.dwak.redditslackbot.slack.model.SlackInfo
import io.dwak.redditslackbot.slack.model.WebHookPayload
import io.reactivex.Completable
import io.reactivex.Single

open class FakeSlackBot : SlackBot{
  override fun login(code: String): Single<SlackInfo> = TODO("not implemented")
  override fun postToChannel(path: String, payload: WebHookPayload): Completable = TODO("not implemented")
  override fun updateMessage(responseUrl: String, payload: WebHookPayload): Completable = TODO("not implemented")
}