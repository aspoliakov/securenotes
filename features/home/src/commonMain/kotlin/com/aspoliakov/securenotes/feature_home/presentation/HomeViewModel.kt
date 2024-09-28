package com.aspoliakov.securenotes.feature_home.presentation

import androidx.lifecycle.viewModelScope
import com.aspoliakov.securenotes.core_presentation.mvi.MviViewModel
import com.aspoliakov.securenotes.core_presentation.utils.launchOnIO
import com.aspoliakov.securenotes.domain_notes.NotesCreateInteractor
import com.aspoliakov.securenotes.domain_notes.NotesListInteractor
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Project SecureNotes
 */
class HomeViewModel(
        initialState: HomeState = HomeState.Idle,
        private val notesListInteractor: NotesListInteractor,
        private val notesCreateInteractor: NotesCreateInteractor,
) : MviViewModel<HomeState, HomeEffect, HomeIntent>(initialState) {

    init {
        notesListInteractor.getNotesListTest()
                .onEach { notesList ->
                    Napier.e("Notes list: $notesList")
                    reduceState {
                        HomeState.Loaded(
                                notesList = notesList,
                        )
                    }
                }
                .flowOn(Dispatchers.IO)
                .launchIn(viewModelScope)
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.AddRandomNote -> launchOnIO {
                notesCreateInteractor.addNewNotes("folder_id")
            }
        }
    }
}
