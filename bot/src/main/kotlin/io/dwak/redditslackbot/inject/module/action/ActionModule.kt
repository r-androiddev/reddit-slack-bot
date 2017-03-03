package io.dwak.redditslackbot.inject.module.action

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.action.AddCannedResponse
import io.dwak.redditslackbot.http.action.FinalizeReddit
import io.dwak.redditslackbot.http.action.GetCannedResponses
import io.dwak.redditslackbot.http.action.Index
import io.dwak.redditslackbot.http.action.OnButton
import io.dwak.redditslackbot.http.action.RedditLogin
import io.dwak.redditslackbot.http.action.RemoveCannedResponse
import io.dwak.redditslackbot.http.action.SlackLogin
import io.dwak.redditslackbot.http.action.SlashCommand

@Module
abstract class ActionModule {

  @Binds
  @IntoSet
  abstract fun index(impl: Index): RequestAction

  @Binds
  @IntoSet
  abstract fun slashCommand(impl: SlashCommand): RequestAction

  @Binds
  @IntoSet
  abstract fun onButton(impl: OnButton): RequestAction

  @Binds
  @IntoSet
  abstract fun redditLogin(impl: RedditLogin): RequestAction

  @Binds
  @IntoSet
  abstract fun slackLogin(impl: SlackLogin): RequestAction

  @Binds
  @IntoSet
  abstract fun finalizeReddit(impl: FinalizeReddit): RequestAction

  @Binds
  @IntoSet
  abstract fun addRule(impl: AddCannedResponse): RequestAction

  @Binds
  @IntoSet
  abstract fun getRules(impl: GetCannedResponses): RequestAction

  @Binds @IntoSet
  abstract fun removeRule(impl: RemoveCannedResponse): RequestAction
}