package com.aspoliakov.securenotes.core_base.util

import java.util.UUID

/**
 * Project SecureNotes
 */

actual fun randomUUIDString(): String = UUID.randomUUID().toString()
