package com.aspoliakov.securenotes.feature_profile.di

import com.aspoliakov.securenotes.feature_profile.presentation.ProfileState
import com.aspoliakov.securenotes.feature_profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val profileViewModelModule = module {
    viewModel {
        ProfileViewModel(
                initialState = ProfileState(),
                userStateProvider = get(),
                userStateInteractor = get(),
        )
    }
}
