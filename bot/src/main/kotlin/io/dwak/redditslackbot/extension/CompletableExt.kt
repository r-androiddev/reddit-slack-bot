package io.dwak.redditslackbot.extension

import io.reactivex.Completable
import org.slf4j.Logger

fun Completable.log(logger: Logger, message: String = "") = this.doOnComplete { logger.debug(message) }

