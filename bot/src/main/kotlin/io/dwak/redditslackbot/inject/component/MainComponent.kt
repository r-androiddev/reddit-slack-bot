package io.dwak.redditslackbot.inject.component

import dagger.Component
import io.dwak.redditslackbot.http.Bot
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.inject.annotation.qualifier.AppConfig
import io.dwak.redditslackbot.inject.annotation.qualifier.FirebaseConfig
import io.dwak.redditslackbot.inject.annotation.qualifier.reddit.RedditConfig
import io.dwak.redditslackbot.inject.annotation.qualifier.slack.SlackConfig
import io.dwak.redditslackbot.inject.module.MainModule
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(MainModule::class))
interface MainComponent {
  fun bot(): Bot

  @AppConfig
  fun appConfig(): Map<String, String>

  @RedditConfig
  fun redditConfig(): Map<String, String>

  @SlackConfig
  fun slackConfig(): Map<String, String>

  @FirebaseConfig
  fun firebaseConfig(): Map<String, String>
}