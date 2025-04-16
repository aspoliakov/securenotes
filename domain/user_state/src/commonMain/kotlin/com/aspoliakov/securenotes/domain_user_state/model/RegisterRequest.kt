package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class RegisterRequest(
        @SerialName("email") val email: String,
        @SerialName("password") val password: String,
)
