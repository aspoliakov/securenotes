package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.runtime.Composable
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.ThemePreview

/**
 * Project SecureNotes
 */

@ThemePreview
@Composable
fun ScrollableTabRowPreview() {
    AppTheme {
        ScrollableTabRow(
                tabs = listOf("Tab1", "Tab2", "Tab3"),
                selectedTabIndex = 0,
        )
    }
}
