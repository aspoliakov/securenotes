package com.aspoliakov.securenotes.core_base

/**
 * Project Express. Created by Unlimited Production team.
 */

interface DeviceConfigProvider {

    fun getDeviceInfo(): DeviceInfo
}

data class DeviceInfo(
        val name: String,
        val platform: String,
        val osVersion: String,
)
