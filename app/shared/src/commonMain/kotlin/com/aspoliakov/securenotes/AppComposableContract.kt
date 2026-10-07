package com.aspoliakov.securenotes

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State
import com.aspoliakov.securenotes.domain_user_state.model.AppThemeMode
import com.aspoliakov.securenotes.domain_user_state.model.UserState

/**
 * Project SecureNotes
 */

data class AppComposableState(
        val globalState: AppGlobalState,
        val themeMode: AppThemeMode,
) : State()

sealed class AppGlobalState {
    data object Auth : AppGlobalState()
    data object Keys : AppGlobalState()
    data object Active : AppGlobalState()
}

fun UserState.toAppGlobalState(): AppGlobalState {
    return when (this) {
        UserState.AUTH -> AppGlobalState.Auth
        UserState.KEYS -> AppGlobalState.Keys
        UserState.ACTIVE -> AppGlobalState.Active
    }
}

object AppComposableEffect : Effect()

object AppComposableIntent : Intent()
