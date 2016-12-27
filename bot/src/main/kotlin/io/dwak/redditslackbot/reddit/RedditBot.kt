package io.dwak.redditslackbot.reddit

import io.dwak.redditslackbot.inject.annotation.qualifier.reddit.RedditConfig
import io.dwak.redditslackbot.reddit.network.service.RedditLoginService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedditBot @Inject constructor(val redditService: RedditService,
                                    val redditLoginService: RedditLoginService,
                                    @RedditConfig val config: Map<String, String>) {
}