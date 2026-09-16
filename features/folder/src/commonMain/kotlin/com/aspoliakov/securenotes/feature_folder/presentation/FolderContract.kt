package com.aspoliakov.securenotes.feature_folder.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.Effect
import com.aspoliakov.securenotes.core_presentation.mvi.Intent
import com.aspoliakov.securenotes.core_presentation.mvi.State

/**
 * Project SecureNotes
 */

sealed class FolderMode {
    data class Create(val parentId: String?) : FolderMode()
    data class Edit(val folderId: String) : FolderMode()
}

data class FolderState(
        val mode: FolderMode,
        val name: String = "",
) : State()

sealed class FolderEffect : Effect() {
    data object Dismiss : FolderEffect()
}

sealed class FolderIntent : Intent() {
    data class OnNameChanged(val name: String) : FolderIntent()
    data object OnConfirmClick : FolderIntent()
    data object OnCancelClick : FolderIntent()
}
