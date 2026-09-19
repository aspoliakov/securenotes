package com.aspoliakov.securenotes.core_presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
sealed class AppGlobalScreen {
    @Serializable
    data object Auth : AppGlobalScreen()

    @Serializable
    data object Keys : AppGlobalScreen()

    @Serializable
    data object Main : AppGlobalScreen()
}
