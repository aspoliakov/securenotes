package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class AuthGoogleResponse(
        @SerialName("message") val message: String,
        @SerialName("user") val user: UserDTO,
        @SerialName("token") val accessToken: String,
        @SerialName("refresh_token") val refreshToken: String,
        @SerialName("session_id") val sessionId: String,
)
