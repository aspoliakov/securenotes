package com.aspoliakov.securenotes.feature_profile.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

sealed class ProfileState : MviState() {
    data object Idle : ProfileState()
}

sealed class ProfileEffect : MviEffect()

sealed class ProfileIntent : MviIntent()
