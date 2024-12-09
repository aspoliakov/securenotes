package com.aspoliakov.securenotes

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState
import com.aspoliakov.securenotes.domain_user_state.model.UserState

/**
 * Project SecureNotes
 */

sealed class AppComposableState : MviState() {
    data object Unauthorized : AppComposableState()
    data object Authorized : AppComposableState()
    data object Active : AppComposableState()
}

fun UserState.toAppState(): AppComposableState {
    return when (this) {
        UserState.UNAUTHORIZED -> AppComposableState.Unauthorized
        UserState.AUTHORIZED -> AppComposableState.Authorized
        UserState.ACTIVE -> AppComposableState.Active
    }
}

object AppComposableEffect : MviEffect()

object AppComposableIntent : MviIntent()
