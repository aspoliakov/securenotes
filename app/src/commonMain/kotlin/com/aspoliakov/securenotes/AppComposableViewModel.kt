package com.aspoliakov.securenotes

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_base.util.flowOnMain
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.domain_user_state.UserStateProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */

class AppComposableViewModel(
        private val userStateProvider: UserStateProvider,
        initialState: AppComposableState,
) : MviViewModel<AppComposableState, AppComposableEffect, AppComposableIntent>(initialState) {

    override fun initData() {
        userStateProvider.observeUserState()
                .onEach { reduceState { it.toAppState() } }
                .flowOnMain()
                .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: AppComposableIntent) {
    }
}
