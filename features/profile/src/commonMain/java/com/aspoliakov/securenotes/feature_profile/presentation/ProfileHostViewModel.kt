package com.aspoliakov.securenotes.feature_profile.presentation.screens

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel

class ProfileHostViewModel(
        initialState: ProfileHostState,
) : MviViewModel<ProfileHostState, ProfileHostEffect, ProfileHostIntent>(initialState) {

    override fun handleIntent(intent: ProfileHostIntent) {
        when (intent) {
            else -> {}
        }
    }
}
