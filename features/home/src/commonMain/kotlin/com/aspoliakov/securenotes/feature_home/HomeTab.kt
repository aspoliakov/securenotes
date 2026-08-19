package com.aspoliakov.securenotes.feature_home

import kotlinx.serialization.Serializable

/**
 * Project SecureNotes
 */

@Serializable
sealed class HomeTab {
    @Serializable
    data object Notes : HomeTab()

    @Serializable
    data object Profile : HomeTab()
}
