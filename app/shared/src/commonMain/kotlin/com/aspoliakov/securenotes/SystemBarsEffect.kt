package com.aspoliakov.securenotes

import androidx.compose.runtime.Composable

/**
 * Project SecureNotes
 */

/**
 * Styles platform system bars for the app theme, which may differ from the system one.
 */
@Composable
internal expect fun SystemBarsEffect(darkTheme: Boolean)
