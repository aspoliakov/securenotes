package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class UserDTO(
        @SerialName("user_id") val userId: String,
        @SerialName("created_at") val createdAt: String,
        @SerialName("updated_at") val updatedAt: String?,
        @SerialName("email") val email: String,
        @SerialName("login") val login: String?,
        @SerialName("role") val role: Int,
        @SerialName("avatar") val avatar: String?,
)
