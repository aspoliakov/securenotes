package com.aspoliakov.securenotes.feature_profile.presentation

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_base.util.flowOnMain
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_user_state.UserLogoutInteractor
import com.aspoliakov.securenotes.domain_user_state.UserPrefsInteractor
import com.aspoliakov.securenotes.domain_user_state.UserStateProvider
import com.aspoliakov.securenotes.domain_user_state.model.AppThemeMode
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProfileViewModel(
        initialState: ProfileState,
        private val userStateProvider: UserStateProvider,
        private val userPrefsInteractor: UserPrefsInteractor,
        private val userLogoutInteractor: UserLogoutInteractor,
) : MviViewModel<ProfileState, ProfileEffect, ProfileIntent>(initialState) {

    init {
        launchOnIO {
            val userProfileData = userStateProvider.getUserProfileData()
            reduceState {
                copy(
                        profileDataState = ProfileDataState.Loaded(
                                name = userProfileData.displayName,
                                avatar = null,
                        )
                )
            }
        }
        userPrefsInteractor.observeAppThemeMode()
            .onEach { reduceState { copy(themeMode = it) } }
            .flowOnMain()
            .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.OnThemeClick -> onThemeClick()
            is ProfileIntent.OnThemeSelected -> onThemeSelected(intent.themeMode)
            is ProfileIntent.OnThemeDialogDismissed -> onThemeDialogDismissed()
            is ProfileIntent.OnLogoutClick -> onLogoutClick()
        }
    }

    private fun onThemeClick() {
        reduceState { copy(isThemeDialogVisible = true) }
    }

    private fun onThemeSelected(themeMode: AppThemeMode) {
        reduceState {
            copy(
                    themeMode = themeMode,
                    isThemeDialogVisible = false,
            )
        }
        launchOnIO { userPrefsInteractor.setAppThemeMode(themeMode) }
    }

    private fun onThemeDialogDismissed() {
        reduceState { copy(isThemeDialogVisible = false) }
    }

    private fun onLogoutClick() {
        launchOnIO { userLogoutInteractor.logout() }
    }
}
