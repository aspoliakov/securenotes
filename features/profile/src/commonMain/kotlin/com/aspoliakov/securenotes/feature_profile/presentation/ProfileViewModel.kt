package com.aspoliakov.securenotes.feature_profile.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_user_state.UserStateInteractor
import com.aspoliakov.securenotes.domain_user_state.UserStateProvider

class ProfileViewModel(
        initialState: ProfileState,
        private val userStateProvider: UserStateProvider,
        private val userStateInteractor: UserStateInteractor,
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
        userStateInteractor.logout()
    }
}
