package com.aspoliakov.securenotes.core_ui.component

import androidx.compose.runtime.Composable
import com.aspoliakov.securenotes.core_ui.AppTheme
import com.aspoliakov.securenotes.core_ui.ThemePreview

/**
 * Project SecureNotes
 */

@ThemePreview
@Composable
fun TopAppBarPreview() {
    AppTheme {
        TopAppBar(
                title = "Title1 Title2 Title3 Title4 Title5 Title6",
        )
    }
}
