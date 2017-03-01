package io.dwak.redditslackbot.extension

import io.reactivex.Single
import org.slf4j.Logger

fun <T> Single<T>.log(logger: Logger, message: String = "")
    = this.doOnSuccess { logger.debug("$message ${it.toString()}") }

