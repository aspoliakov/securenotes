package com.aspoliakov.securenotes.core_presentation.navigation

/**
 * Project SecureNotes
 */

sealed class AppGlobalScreen {
    data object Auth : AppGlobalScreen()
    data object Keys : AppGlobalScreen()
    data object Main : AppGlobalScreen()
}
