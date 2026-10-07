package com.aspoliakov.securenotes

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_base.util.flowOnMain
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.domain_user_state.UserPrefsInteractor
import com.aspoliakov.securenotes.domain_user_state.UserStateProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */

class AppComposableViewModel(
        userStateProvider: UserStateProvider,
        userPrefsInteractor: UserPrefsInteractor,
        initialState: AppComposableState,
) : MviViewModel<AppComposableState, AppComposableEffect, AppComposableIntent>(initialState) {

    init {
        userStateProvider.observeUserState()
            .onEach { reduceState { copy(globalState = it.toAppGlobalState()) } }
            .flowOnMain()
            .launchIn(viewModelScope)
        userPrefsInteractor.observeAppThemeMode()
            .onEach { reduceState { copy(themeMode = it) } }
            .flowOnMain()
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: AppComposableIntent) {
    }
}
