package io.dwak.redditslackbot.slack

import io.dwak.redditslackbot.slack.model.SlackInfo
import io.dwak.redditslackbot.slack.model.WebHookPayload
import io.reactivex.Completable
import io.reactivex.Single


interface SlackBot {
  fun login(code: String): Single<SlackInfo>

  fun postToChannel(path: String, payload: WebHookPayload): Completable

  fun updateMessage(responseUrl: String, payload: WebHookPayload): Completable
}