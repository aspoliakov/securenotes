package com.aspoliakov.securenotes.core_base

import platform.UIKit.UIDevice

/**
 * Project SecureNotes
 */

private const val PLATFORM_IOS = "ios"

internal class PlatformDeviceConfigProvider : DeviceConfigProvider {

    override fun getDeviceInfo(): DeviceInfo {
        val device = UIDevice.currentDevice
        return DeviceInfo(
                // since iOS 16 this is the generic model name ("iPhone"), not the user-assigned one
                name = device.name,
                platform = PLATFORM_IOS,
                osVersion = device.systemVersion,
        )
    }
}
