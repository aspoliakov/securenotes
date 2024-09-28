package com.aspoliakov.securenotes.core_base.util

import platform.Foundation.NSUUID

/**
 * Project SecureNotes
 */

actual fun randomUUIDString(): String = NSUUID().UUIDString()
