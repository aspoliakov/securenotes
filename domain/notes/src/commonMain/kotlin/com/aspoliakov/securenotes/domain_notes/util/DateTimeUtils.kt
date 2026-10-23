package com.aspoliakov.securenotes.domain_notes.util

import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * Project SecureNotes
 */

/**
 * Server timestamps are ISO date-times without an offset, in UTC.
 */
internal fun String.toEpochMillis(): Long {
    return LocalDateTime.parse(this).toInstant(TimeZone.UTC).toEpochMilliseconds()
}

/**
 * ISO date-time in UTC with an offset, as the server accepts it.
 */
internal fun Long.toServerDateTime(): String {
    return Instant.fromEpochMilliseconds(this).toString()
}
