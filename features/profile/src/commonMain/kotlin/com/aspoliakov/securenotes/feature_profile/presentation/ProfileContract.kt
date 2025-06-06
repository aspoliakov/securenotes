package com.aspoliakov.securenotes.feature_profile.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State

data class ProfileState(
        val profileDataState: ProfileDataState = ProfileDataState.Idle,
) : State()

sealed class ProfileDataState {
    data object Idle : ProfileDataState()
    data class Loaded(
            val name: String,
            val avatar: String?,
    ) : ProfileDataState()
}

sealed class ProfileEffect : Effect()

sealed class ProfileIntent : Intent() {
    data object OnLogoutClick : ProfileIntent()
}
