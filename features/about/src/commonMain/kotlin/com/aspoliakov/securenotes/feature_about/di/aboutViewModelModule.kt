package com.aspoliakov.securenotes.feature_about.di

import com.aspoliakov.securenotes.feature_about.presentation.AboutState
import com.aspoliakov.securenotes.feature_about.presentation.AboutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val aboutViewModelModule = module {
    viewModel {
        AboutViewModel(
                initialState = AboutState(
                        appVersion = "0.1",
                ),
        )
    }
}
