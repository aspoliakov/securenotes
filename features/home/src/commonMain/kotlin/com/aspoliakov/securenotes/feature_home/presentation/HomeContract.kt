package com.aspoliakov.securenotes.feature_home.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State

/**
 * Project SecureNotes
 */

sealed class HomeState : State() {
    data object Idle : HomeState()
}

sealed class HomeEffect : Effect()

sealed class HomeIntent : Intent()
