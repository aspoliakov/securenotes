package com.aspoliakov.securenotes.core_presentation.navigation

/**
 * Project SecureNotes
 */

sealed class Screen {
    data object Home : Screen()
    data object Note : Screen() {
        const val ARG_NOTE_ID = "note_id"
    }

    data object About : Screen()
}
