package com.aspoliakov.securenotes.feature_note.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * Project SecureNotes
 *
 * Search in the note body: matches are case-insensitive and don't overlap. Navigation goes from the body
 * selection, so there is no match index to keep in sync with edits.
 */

@Composable
internal fun NoteSearchBar(
        modifier: Modifier = Modifier,
        initialQuery: String,
        hasMatches: Boolean,
        onQueryChange: (String) -> Unit,
        onPreviousClick: () -> Unit,
        onNextClick: () -> Unit,
        onClose: () -> Unit,
) {
    // Kept here, not read back from the state: a delayed round trip of keystrokes breaks the input.
    var query by remember { mutableStateOf(initialQuery) }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    TextField(
            value = query,
            onValueChange = {
                query = it
                onQueryChange(it)
            },
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .focusRequester(focusRequester),
            singleLine = true,
            placeholder = { Text(text = stringResource(Res.string.feature_note_search)) },
            leadingIcon = {
                Icon(
                        imageVector = Icons.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingIcon = {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                            enabled = hasMatches,
                            onClick = onPreviousClick,
                    ) {
                        Icon(
                                imageVector = Icons.SearchPrevious,
                                contentDescription = stringResource(Res.string.feature_note_search_previous),
                        )
                    }
                    IconButton(
                            enabled = hasMatches,
                            onClick = onNextClick,
                    ) {
                        Icon(
                                imageVector = Icons.SearchNext,
                                contentDescription = stringResource(Res.string.feature_note_search_next),
                        )
                    }
                    IconButton(
                            onClick = onClose,
                    ) {
                        Icon(
                                imageVector = Icons.Close,
                                contentDescription = stringResource(Res.string.feature_note_search_close),
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { if (hasMatches) onNextClick() }),
            colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
            ),
    )
}

internal fun findMatches(text: String, query: String): List<TextRange> {
    if (query.isEmpty()) return emptyList()
    val matches = mutableListOf<TextRange>()
    var index = text.indexOf(query, ignoreCase = true)
    while (index >= 0) {
        matches += TextRange(index, index + query.length)
        index = text.indexOf(query, startIndex = index + query.length, ignoreCase = true)
    }
    return matches
}

/** The first match starting at [from] or later, wrapping around to the first one. */
internal fun nextMatch(matches: List<TextRange>, from: Int): TextRange? {
    return matches.firstOrNull { it.start >= from } ?: matches.firstOrNull()
}

/** The match after the [selection]: if it is a match itself, the one after it. */
internal fun nextMatch(matches: List<TextRange>, selection: TextRange): TextRange? {
    val from = if (selection in matches) selection.start + 1 else selection.min
    return nextMatch(matches, from)
}

/** The last match before the [selection], wrapping around to the last one. */
internal fun previousMatch(matches: List<TextRange>, selection: TextRange): TextRange? {
    return matches.lastOrNull { it.start < selection.min } ?: matches.lastOrNull()
}
