package com.aspoliakov.securenotes.feature_about.di

import com.aspoliakov.securenotes.core_base.AppConfigProvider
import com.aspoliakov.securenotes.feature_about.presentation.AboutState
import com.aspoliakov.securenotes.feature_about.presentation.AboutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val aboutViewModelModule = module {
    viewModel {
        val appConfigProvider = get<AppConfigProvider>()
        AboutViewModel(
                initialState = AboutState(
                        appVersion = appConfigProvider.getAppVersion(),
                ),
        )
    }
}
