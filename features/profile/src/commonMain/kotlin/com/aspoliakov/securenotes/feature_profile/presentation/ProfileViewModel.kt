package com.aspoliakov.securenotes.feature_profile.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_user_state.UserLogoutInteractor
import com.aspoliakov.securenotes.domain_user_state.UserStateProvider

class ProfileViewModel(
        initialState: ProfileState,
        private val userStateProvider: UserStateProvider,
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
    }

    override fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.OnLogoutClick -> onLogoutClick()
        }
    }

    private fun onLogoutClick() = launchOnIO {
        userLogoutInteractor.logout()
    }
}
