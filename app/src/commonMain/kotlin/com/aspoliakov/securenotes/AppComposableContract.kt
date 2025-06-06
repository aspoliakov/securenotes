package com.aspoliakov.securenotes

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State
import com.aspoliakov.securenotes.domain_user_state.model.UserState

/**
 * Project SecureNotes
 */

sealed class AppComposableState : State() {
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

object AppComposableEffect : Effect()

object AppComposableIntent : Intent()
