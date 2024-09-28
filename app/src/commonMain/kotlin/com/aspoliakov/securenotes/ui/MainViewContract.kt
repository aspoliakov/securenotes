package com.aspoliakov.securenotes.ui

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

/**
 * Project SecureNotes
 */

sealed class MainViewState : MviState() {
    data object Loading : MainViewState()
    data object Unauthorized : MainViewState()
    data object Authorized : MainViewState()
}

object MainViewEffect : MviEffect()

object MainViewIntent : MviIntent()
