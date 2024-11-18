package com.aspoliakov.securenotes.feature_keys.di

import com.aspoliakov.securenotes.feature_keys.presentation.KeysState
import com.aspoliakov.securenotes.feature_keys.presentation.KeysViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val keysViewModelModule = module {
    viewModel {
        KeysViewModel(
                initialState = KeysState.Loading,
                userKeysInteractor = get(),
                userStateInteractor = get(),
        )
    }
}
