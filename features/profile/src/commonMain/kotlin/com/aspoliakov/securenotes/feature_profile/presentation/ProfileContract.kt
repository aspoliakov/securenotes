package com.aspoliakov.securenotes.feature_profile.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State
import com.aspoliakov.securenotes.domain_user_state.model.AppThemeMode

data class ProfileState(
        val profileDataState: ProfileDataState = ProfileDataState.Idle,
        val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
        val isThemeDialogVisible: Boolean = false,
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
    data object OnThemeClick : ProfileIntent()
    data class OnThemeSelected(val themeMode: AppThemeMode) : ProfileIntent()
    data object OnThemeDialogDismissed : ProfileIntent()
    data object OnLogoutClick : ProfileIntent()
}
