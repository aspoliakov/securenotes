package com.aspoliakov.securenotes.feature_folder.di

import com.aspoliakov.securenotes.feature_folder.presentation.FolderMode
import com.aspoliakov.securenotes.feature_folder.presentation.FolderState
import com.aspoliakov.securenotes.feature_folder.presentation.FolderViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Project SecureNotes
 */

val folderViewModelModule = module {
    viewModel { params ->
        val mode = params.get<FolderMode>()
        FolderViewModel(
                initialState = FolderState(mode = mode),
                folderInteractor = get(),
        )
    }
}
