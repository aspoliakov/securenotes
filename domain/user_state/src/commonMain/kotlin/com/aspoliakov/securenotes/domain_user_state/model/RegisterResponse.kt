package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class RegisterResponse(
        @SerialName("message") val message: String,
        @SerialName("user") val user: UserDTO,
        @SerialName("token") val token: String,
) {
    companion object {
        const val ERROR_USER_ALREADY_REGISTERERD = "user already registererd"
    }
}
