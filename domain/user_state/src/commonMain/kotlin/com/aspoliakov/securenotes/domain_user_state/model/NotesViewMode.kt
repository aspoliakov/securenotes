package com.aspoliakov.securenotes.domain_user_state.model

/**
 * Project SecureNotes
 */

enum class NotesViewMode {

    LIST,
    GRID;

    companion object {

        fun fromName(name: String?): NotesViewMode {
            return entries.firstOrNull { it.name == name } ?: LIST
        }
    }
}
