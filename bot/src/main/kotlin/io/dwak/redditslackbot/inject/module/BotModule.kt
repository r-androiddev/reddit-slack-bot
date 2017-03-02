package io.dwak.redditslackbot.inject.module

import dagger.Binds
import dagger.Module
import io.dwak.redditslackbot.reddit.RedditBot
import io.dwak.redditslackbot.reddit.RedditBotImpl
import io.dwak.redditslackbot.slack.SlackBot
import io.dwak.redditslackbot.slack.SlackBotImpl
import javax.inject.Singleton


@Module
abstract class BotModule {
  @Binds @Singleton abstract fun bindRedditBot(impl: RedditBotImpl): RedditBot
  @Binds @Singleton abstract fun bindSlackBot(impl: SlackBotImpl): SlackBot
}