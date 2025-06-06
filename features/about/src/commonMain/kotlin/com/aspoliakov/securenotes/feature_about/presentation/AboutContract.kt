package com.aspoliakov.securenotes.feature_about.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State

/**
 * Project SecureNotes
 */

data class AboutState(
        val appVersion: String = "stub",
) : State()

sealed class AboutEffect : Effect() {
    data class ShowSnackbar(val message: String) : AboutEffect()
}

object AboutIntent : Intent()
