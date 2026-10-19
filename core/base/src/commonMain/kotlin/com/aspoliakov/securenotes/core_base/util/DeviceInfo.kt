package com.aspoliakov.securenotes.core_base.util

/**
 * Project SecureNotes
 */

data class DeviceInfo(
        val name: String,
        val platform: String,
        val osVersion: String,
)

expect fun currentDeviceInfo(): DeviceInfo
