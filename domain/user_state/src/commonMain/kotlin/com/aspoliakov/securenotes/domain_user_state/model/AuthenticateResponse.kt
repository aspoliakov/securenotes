package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class AuthenticateResponse(
        @SerialName("message") val message: String,
        @SerialName("user") val user: UserDTO,
        @SerialName("token") val token: String,
) {
    companion object {
        const val ERROR_WRONG_CREDENTIALS = "wrong credentials"
    }
}
