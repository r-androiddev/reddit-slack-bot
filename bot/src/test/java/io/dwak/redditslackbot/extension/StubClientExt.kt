package io.dwak.redditslackbot.extension

import com.spotify.apollo.Request
import com.spotify.apollo.RequestContext
import com.spotify.apollo.RequestMetadata
import com.spotify.apollo.request.RequestContexts
import com.spotify.apollo.request.RequestMetadataImpl
import com.spotify.apollo.test.StubClient
import java.time.Instant
import java.util.*

fun StubClient.createRequestContext(req: Request,
                                    pathArgs: MutableMap<String, String> = mutableMapOf(),
                                    arrivalTime: Long = Instant.now().nano.toLong(),
                                    requestMetadata: RequestMetadata = RequestMetadataImpl.create(Instant.now(),
                                        Optional.empty(),
                                        Optional.empty()))
    : RequestContext = RequestContexts.create(req, this, pathArgs, arrivalTime, requestMetadata)

