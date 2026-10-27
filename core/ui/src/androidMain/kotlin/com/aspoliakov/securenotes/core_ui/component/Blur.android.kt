package com.aspoliakov.securenotes.core_ui.component

import android.os.Build

/**
 * Project SecureNotes
 */

internal actual val isBlurSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
