package com.aspoliakov.securenotes.domain_user_state.model

import kotlin.time.Instant

/**
 * Project SecureNotes
 */

data class ActiveSession(
        val id: String,
        val deviceName: String?,
        val platform: SessionPlatform,
        val osVersion: String?,
        val createdAt: Instant,
        val lastActiveAt: Instant,
        val isCurrent: Boolean,
)

enum class SessionPlatform {
    ANDROID,
    IOS,
    UNKNOWN,
}
