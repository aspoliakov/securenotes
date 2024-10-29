package com.aspoliakov.securenotes.feature_profile.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

data class ProfileState(
        val profileDataState: ProfileDataState = ProfileDataState.Idle,
) : MviState()

sealed class ProfileDataState {
    data object Idle : ProfileDataState()
    data class Loaded(
            val name: String,
            val avatar: String?,
    ) : ProfileDataState()
}

sealed class ProfileEffect : MviEffect()

sealed class ProfileIntent : MviIntent() {
    data object OnAboutClick : ProfileIntent()
    data object OnLogoutClick : ProfileIntent()
}
