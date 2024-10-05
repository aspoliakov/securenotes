package com.aspoliakov.securenotes.feature_profile.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel

class ProfileViewModel(
        initialState: ProfileState,
) : MviViewModel<ProfileState, ProfileEffect, ProfileIntent>(initialState) {

    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            else -> {}
        }
    }
}
