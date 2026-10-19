package com.aspoliakov.securenotes.core_base.util

import android.os.Build

/**
 * Project SecureNotes
 */

private const val PLATFORM_ANDROID = "android"

actual fun currentDeviceInfo(): DeviceInfo {
    val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercaseChar() }
    // some models already include the brand, the rest get it prepended: "Google Pixel 8"
    val name = if (Build.MODEL.startsWith(Build.MANUFACTURER, ignoreCase = true)) {
        Build.MODEL
    } else {
        "$manufacturer ${Build.MODEL}"
    }
    return DeviceInfo(
            name = name,
            platform = PLATFORM_ANDROID,
            osVersion = Build.VERSION.RELEASE,
    )
}
