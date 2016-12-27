package io.dwak.redditslackbot.inject.annotation.qualifier.slack

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SlackConfig()

