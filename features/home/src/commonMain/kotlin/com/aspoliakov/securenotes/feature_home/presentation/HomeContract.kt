package com.aspoliakov.securenotes.feature_home.presentation

import com.aspoliakov.securenotes.core_presentation.mvi.MviEffect
import com.aspoliakov.securenotes.core_presentation.mvi.MviIntent
import com.aspoliakov.securenotes.core_presentation.mvi.MviState
import com.aspoliakov.securenotes.domain_notes.data.folders.NotesListItem

/**
 * Project SecureNotes
 */

sealed class HomeState : MviState() {
    data object Idle : HomeState()
    data class Loaded(
            private val notesList: List<NotesListItem>
    ) : HomeState()
}

sealed class HomeEffect : MviEffect()

sealed class HomeIntent : MviIntent() {
    data object AddRandomNote : HomeIntent()
}
