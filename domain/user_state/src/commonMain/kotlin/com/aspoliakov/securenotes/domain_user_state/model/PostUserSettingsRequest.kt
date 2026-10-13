package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class PostUserSettingsRequest(
        @SerialName("settings") val settings: List<PostUserSettingItem>,
)

@Serializable
data class PostUserSettingItem(
        @SerialName("key") val key: String,
        @SerialName("value") val value: String,
)
