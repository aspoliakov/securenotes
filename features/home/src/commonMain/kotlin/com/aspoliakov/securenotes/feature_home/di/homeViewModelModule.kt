package com.aspoliakov.securenotes.feature_home.di

import com.aspoliakov.securenotes.feature_home.presentation.HomeState
import com.aspoliakov.securenotes.feature_home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val homeViewModelModule = module {
    viewModel {
        HomeViewModel(
                initialState = HomeState.Idle,
        )
    }
}
