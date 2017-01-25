package io.dwak.redditslackbot.http.action

import com.spotify.apollo.RequestContext
import com.squareup.moshi.Moshi
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.completableFuture
import io.dwak.redditslackbot.inject.annotation.qualifier.slack.SlackConfig
import io.dwak.redditslackbot.inject.module.config.ConfigValues
import io.dwak.redditslackbot.reddit.RedditBot
import io.dwak.redditslackbot.slack.SlackBot
import io.dwak.redditslackbot.slack.model.ButtonAction
import io.dwak.redditslackbot.slack.model.SlackMessagePayload
import io.reactivex.Completable
import io.reactivex.Observable
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


class OnButton @Inject constructor(private val redditBot: RedditBot,
                                   private val moshi: Moshi,
                                   @SlackConfig private val slackConfig: Map<String, String>)
  : RequestAction {

  override val name = "on-button"
  override val method = "POST"

  val verificationToken by lazy { slackConfig[ConfigValues.Slack.VERIFICATION_TOKEN] }

  override val action: (RequestContext) -> CompletableFuture<String> = {
    completableFuture(it) { req, future ->
      Observable.just(it.request().payload().get().utf8())
          .map { it.substring(8) } //Slack prepends payload with `payload=` thanks Slack.
          .map { URLDecoder.decode(it, "UTF-8") }
          .map {
            moshi.adapter(SlackMessagePayload::class.java)
                .lenient()
                .fromJson(it)
          }
          .filter { it.token == verificationToken }
          .flatMapCompletable {
            val path = "${it.team.id}-${it.channel.id}"
            when(it.actions[0].value) {
              ButtonAction.ACTION_REMOVE.value -> redditBot.selectRemovalReason(path, it)
              ButtonAction.ACTION_FLAIR.value -> redditBot.flairPost(path, it)
              ButtonAction.ACTION_SELECT_FLAIR.value -> redditBot.selectFlair(path, it)
              else -> Completable.complete()
            }
          }
          .subscribe()
      future.complete("")
    }
  }

}