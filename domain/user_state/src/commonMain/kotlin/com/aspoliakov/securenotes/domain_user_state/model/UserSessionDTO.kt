package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class UserSessionDTO(
        @SerialName("session_id") val sessionId: String,
        @SerialName("created_at") val createdAt: String,
        @SerialName("last_active_at") val lastActiveAt: String,
        @SerialName("expires_at") val expiresAt: String,
        @SerialName("device_name") val deviceName: String?,
        @SerialName("platform") val platform: String?,
        @SerialName("os_version") val osVersion: String?,
        @SerialName("app_version") val appVersion: String?,
)
