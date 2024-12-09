package com.aspoliakov.securenotes.feature_home.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

/**
 * Project SecureNotes
 */

sealed class HomeState : MviState() {
    data object Idle : HomeState()
}

sealed class HomeEffect : MviEffect()

sealed class HomeIntent : MviIntent()
