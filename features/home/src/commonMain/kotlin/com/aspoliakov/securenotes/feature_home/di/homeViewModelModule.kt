package com.aspoliakov.securenotes.feature_home.di

import com.aspoliakov.securenotes.feature_home.presentation.HomeState
import com.aspoliakov.securenotes.feature_home.presentation.HomeViewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val homeViewModelModule = module {
    factory {
        HomeViewModel(
                initialState = HomeState.Idle,
                notesListInteractor = get(),
                notesCreateInteractor = get(),
        )
    }
}
