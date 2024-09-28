package com.aspoliakov.securenotes.ui

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import com.aspoliakov.securenotes.domain_user_state.model.UserState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */

class MainViewModel(
        userStateInteractor: UserStateInteractor,
) : MviViewModel<MainViewState, MainViewEffect, MainViewIntent>(MainViewState.Loading) {

    init {
        userStateInteractor.getUserState()
                .onEach {
                    reduceState {
                        Napier.e("UserState: $it")
                        when (it) {
                            UserState.UNAUTHORIZED -> MainViewState.Unauthorized
                            UserState.AUTHORIZED -> MainViewState.Authorized
                        }
                    }
                }
                .flowOn(Dispatchers.Main)
                .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: MainViewIntent) {
    }
}
