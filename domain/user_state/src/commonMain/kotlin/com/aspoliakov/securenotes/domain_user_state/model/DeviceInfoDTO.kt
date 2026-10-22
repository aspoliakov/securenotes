package com.aspoliakov.securenotes.domain_user_state.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
data class DeviceInfoDTO(
        @SerialName("device_name") val deviceName: String,
        @SerialName("platform") val platform: String,
        @SerialName("os_version") val osVersion: String,
        @SerialName("app_version") val appVersion: String,
)
