package com.aspoliakov.securenotes.feature_auth.di

import com.aspoliakov.securenotes.feature_auth.presentation.AuthState
import com.aspoliakov.securenotes.feature_auth.presentation.AuthViewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val authViewModelModule = module {
    factory {
        AuthViewModel(
                initialState = AuthState(),
                userStateInteractor = get(),
        )
    }
}
