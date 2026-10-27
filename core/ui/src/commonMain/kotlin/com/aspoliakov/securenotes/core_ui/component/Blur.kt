package com.aspoliakov.securenotes.core_ui.component

/**
 * Project SecureNotes
 */

/** Whether `RenderEffect` blur is drawn: on Android it needs API 31, below that blur modifiers are a no-op. */
internal expect val isBlurSupported: Boolean
