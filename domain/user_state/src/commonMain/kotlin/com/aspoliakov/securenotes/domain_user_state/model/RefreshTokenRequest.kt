package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class RefreshTokenRequest(
        @SerialName("refresh_token") val refreshToken: String,
        @SerialName("app_version") val appVersion: String,
)
