package com.aspoliakov.securenotes.domain_user_state.model

/**
 * Project SecureNotes
 */

enum class AppThemeMode {

    SYSTEM,
    LIGHT,
    DARK;

    companion object {

        fun fromName(name: String?): AppThemeMode {
            return entries.firstOrNull { it.name == name } ?: SYSTEM
        }
    }
}
