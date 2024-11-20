package com.aspoliakov.securenotes.feature_about.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

/**
 * Project SecureNotes
 */

data class AboutState(
        val appVersion: String = "stub",
) : MviState()

sealed class AboutEffect : MviEffect() {
    data class ShowSnackbar(val message: String) : AboutEffect()
}

object AboutIntent : MviIntent()
