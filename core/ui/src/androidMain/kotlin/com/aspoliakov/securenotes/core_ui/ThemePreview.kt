package com.aspoliakov.securenotes.core_ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Project SecureNotes
 */

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark")
annotation class ThemePreview
