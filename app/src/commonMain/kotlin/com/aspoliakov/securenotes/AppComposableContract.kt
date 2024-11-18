package com.aspoliakov.securenotes

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

/**
 * Project SecureNotes
 */

sealed class AppComposableState : MviState() {
    data object Loading : AppComposableState()
    data object Unauthorized : AppComposableState()
    data object Authorized : AppComposableState()
    data object Active : AppComposableState()
}

object AppComposableEffect : MviEffect()

object AppComposableIntent : MviIntent()
