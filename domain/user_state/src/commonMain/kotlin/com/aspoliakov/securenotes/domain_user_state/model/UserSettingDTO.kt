package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class UserSettingDTO(
        @SerialName("key") val key: String,
        @SerialName("value") val value: String,
        @SerialName("updated_at") val updatedAt: String,
)
