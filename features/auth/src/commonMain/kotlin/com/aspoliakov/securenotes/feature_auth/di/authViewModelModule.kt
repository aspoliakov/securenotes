package com.aspoliakov.securenotes.feature_auth.di

import com.aspoliakov.securenotes.feature_auth.presentation.AuthState
import com.aspoliakov.securenotes.feature_auth.presentation.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val authViewModelModule = module {
    viewModel {
        AuthViewModel(
                initialState = AuthState(),
                userStateInteractor = get(),
        )
    }
}
