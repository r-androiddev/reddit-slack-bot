package io.dwak.redditslackbot.extension

import com.typesafe.config.Config
import io.dwak.redditslackbot.inject.module.config.ConfigValues

fun Config.getStringFor(configValues: ConfigValues, value: String): String
    = getString("${configValues.PATH}.$value")
