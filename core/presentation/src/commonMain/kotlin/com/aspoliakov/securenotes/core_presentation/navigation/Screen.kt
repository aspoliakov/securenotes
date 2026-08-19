package com.aspoliakov.securenotes.core_presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data class Note(val noteId: String? = null) : Screen()

    @Serializable
    data object About : Screen()
}
