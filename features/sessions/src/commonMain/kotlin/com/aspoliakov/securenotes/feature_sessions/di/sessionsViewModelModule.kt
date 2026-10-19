package com.aspoliakov.securenotes.feature_sessions.di

import com.aspoliakov.securenotes.feature_sessions.presentation.SessionsState
import com.aspoliakov.securenotes.feature_sessions.presentation.SessionsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val sessionsViewModelModule = module {
    viewModel {
        SessionsViewModel(
                initialState = SessionsState(),
                activeSessionsInteractor = get(),
        )
    }
}
