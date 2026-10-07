package com.aspoliakov.securenotes.core_ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner

/**
 * Project SecureNotes
 */

@Composable
fun AppPreview(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
            LocalNavigationEventDispatcherOwner provides rememberNavigationEventDispatcherOwner(parent = null),
    ) {
        AppTheme(
                darkTheme = darkTheme,
                content = content,
        )
    }
}
