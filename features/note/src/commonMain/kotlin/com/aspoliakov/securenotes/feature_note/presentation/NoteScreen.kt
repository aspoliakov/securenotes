package com.aspoliakov.securenotes.feature_note.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.koin.compose.koinInject

/**
 * Project SecureNotes
 */

@Composable
fun NoteScreenRoute(
        modifier: Modifier = Modifier,
        noteId: String,
) {
    val viewModel: NoteViewModel = koinInject()
    val state = viewModel.currentState
    NoteScreen(
            modifier = modifier,
            state = state,
            intentHandler = viewModel::handleIntent,
    )
}

@Composable
internal fun NoteScreen(
        modifier: Modifier = Modifier,
        state: NoteState = NoteState.Idle,
        intentHandler: (NoteIntent) -> Unit = {},
) {
    Scaffold(modifier = modifier) { paddings ->
        Column(
                modifier = modifier
                        .padding(paddings)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
        ) {
            Text(
                    text = "Note",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
            )
        }
    }
}
