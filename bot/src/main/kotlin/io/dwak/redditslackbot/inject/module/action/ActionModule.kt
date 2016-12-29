package io.dwak.redditslackbot.inject.module.action

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import io.dwak.redditslackbot.http.RequestAction
import io.dwak.redditslackbot.http.action.CheckPosts
import io.dwak.redditslackbot.http.action.OnButton
import io.dwak.redditslackbot.http.action.RedditLogin
import io.dwak.redditslackbot.http.action.SlackLogin

@Module
abstract class ActionModule {

  @Binds
  @IntoMap @StringKey(CheckPosts.name)
  abstract fun checkPosts(impl: CheckPosts): RequestAction

  @Binds
  @IntoMap @StringKey(OnButton.name)
  abstract fun onButton(impl: OnButton): RequestAction

  @Binds
  @IntoMap @StringKey(RedditLogin.name)
  abstract fun redditLogin(impl: RedditLogin): RequestAction

  @Binds
  @IntoMap @StringKey(SlackLogin.name)
  abstract fun slackLogin(impl: SlackLogin): RequestAction
}