package com.aspoliakov.securenotes.feature_profile.presentation.screens

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState

data class ProfileHostState(
        val currentTabIndex: Int,
) : MviState()

sealed class ProfileHostEffect : MviEffect()

sealed class ProfileHostIntent : MviIntent()
