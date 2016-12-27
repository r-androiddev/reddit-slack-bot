package io.dwak.redditslackbot.inject.annotation.qualifier.slack

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Slack(val oauth: Boolean = false)

