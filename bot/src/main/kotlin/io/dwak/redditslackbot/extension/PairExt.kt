package io.dwak.redditslackbot.extension

import io.reactivex.functions.BiFunction


fun <A, B, C> Pair<A, B>.toTriple(c: C): Triple<A, B, C> = Triple(first, second, c)

object PairUtil {
  @JvmStatic
  fun <A, B> createPair(): BiFunction<A, B, Pair<A, B>> {
    return BiFunction { t, u -> t to u }
  }
}

