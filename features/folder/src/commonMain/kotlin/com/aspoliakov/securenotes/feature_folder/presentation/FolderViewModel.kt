package com.aspoliakov.securenotes.feature_folder.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_folders.FolderInteractor

/**
 * Project SecureNotes
 */

class FolderViewModel(
        initialState: FolderState,
        private val folderInteractor: FolderInteractor,
) : MviViewModel<FolderState, FolderEffect, FolderIntent>(initialState) {

    init {
        val mode = currentState.mode
        if (mode is FolderMode.Edit) {
            launchOnIO {
                val folder = folderInteractor.getFolder(mode.folderId)
                if (folder != null) {
                    reduceState { copy(name = folder.name) }
                }
            }
        }
    }

    override fun handleIntent(intent: FolderIntent) {
        when (intent) {
            is FolderIntent.OnNameChanged -> reduceState { copy(name = intent.name) }
            is FolderIntent.OnConfirmClick -> onConfirmClick()
            is FolderIntent.OnCancelClick -> sendEffect { FolderEffect.Dismiss }
        }
    }

    private fun onConfirmClick() {
        val name = currentState.name.trim()
        if (name.isBlank()) return
        when (val mode = currentState.mode) {
            is FolderMode.Create -> launchOnIO {
                folderInteractor.createNew(parentId = mode.parentId, name = name)
                sendEffect { FolderEffect.Dismiss }
            }
            is FolderMode.Edit -> {
                folderInteractor.rename(mode.folderId, name)
                sendEffect { FolderEffect.Dismiss }
            }
        }
    }
}
