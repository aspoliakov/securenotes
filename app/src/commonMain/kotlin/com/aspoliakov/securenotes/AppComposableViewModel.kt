package com.aspoliakov.securenotes

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_base.util.flowOnMain
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import com.aspoliakov.securenotes.domain_user_state.model.UserState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */

class AppComposableViewModel(
        userStateInteractor: UserStateInteractor,
) : MviViewModel<AppComposableState, AppComposableEffect, AppComposableIntent>(AppComposableState.Loading) {

    init {
        userStateInteractor.getUserState()
                .onEach {
                    reduceState {
                        Napier.e("UserState: $it")
                        when (it) {
                            UserState.UNAUTHORIZED -> AppComposableState.Unauthorized
                            UserState.AUTHORIZED -> AppComposableState.Authorized
                        }
                    }
                }
                .flowOnMain()
                .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: AppComposableIntent) {
    }
}
