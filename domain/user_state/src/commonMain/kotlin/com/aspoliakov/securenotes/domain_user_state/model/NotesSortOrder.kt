package com.aspoliakov.securenotes.domain_user_state.model

/**
 * Project SecureNotes
 */

enum class NotesSortOrder {

    NEWEST_FIRST,
    OLDEST_FIRST,
    CUSTOM;

    companion object {

        fun fromName(name: String?): NotesSortOrder {
            return entries.firstOrNull { it.name == name } ?: NEWEST_FIRST
        }
    }
}
