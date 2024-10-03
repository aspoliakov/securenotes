package com.aspoliakov.securenotes.feature_home.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel

/**
 * Project SecureNotes
 */
class HomeViewModel(
        initialState: HomeState = HomeState.Idle,
) : MviViewModel<HomeState, HomeEffect, HomeIntent>(initialState) {

    override fun handleIntent(intent: HomeIntent) {
    }
}
