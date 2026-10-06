package com.aspoliakov.securenotes.feature_note.presentation.editor

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
import com.aspoliakov.securenotes.feature_note.presentation.styled_text.StyledTextState
import com.aspoliakov.securenotes.feature_note.presentation.styled_text.TextStyleAction
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

/**
 * Project SecureNotes
 *
 * State of the note editor: title, styled body and their shared undo/redo history.
 * The history lives with the editor, not in the ViewModel: it restores cursor and selection and groups
 * typing into steps, which only the editor knows about. It is not saved, so it starts over on recreation.
 */

@Stable
internal class NoteEditorState(
        initialTitle: TextFieldValue,
        val body: StyledTextState,
) {

    companion object {
        private const val MAX_HISTORY_STEPS = 100
        private val TypingStepTimeout = 1.seconds

        val Saver: Saver<NoteEditorState, Any> = listSaver(
                save = { state ->
                    listOf(
                            with(TextFieldValue.Saver) { save(state.title) },
                            with(StyledTextState.Saver) { save(state.body) },
                    )
                },
                restore = { saved ->
                    val title = saved[0]?.let { TextFieldValue.Saver.restore(it) }
                    val body = saved[1]?.let { StyledTextState.Saver.restore(it) }
                    if (title != null && body != null) NoteEditorState(title, body) else null
                },
        )
    }

    var title by mutableStateOf(initialTitle)

    var canUndo by mutableStateOf(false)
        private set

    var canRedo by mutableStateOf(false)
        private set

    private val history = EditHistory(takeSnapshot(), MAX_HISTORY_STEPS)

    /** The last recorded text change; null when the next one must start a new step. */
    private var lastEdit: LastEdit? = null

    /** Applies [action] to the body as a separate undo step. */
    fun applyStyle(action: TextStyleAction) {
        recordChange()
        lastEdit = null
        body.applyStyle(action)
        recordChange()
        lastEdit = null
    }

    fun undo() {
        recordChange()
        history.undo()?.let { restore(it) }
    }

    fun redo() {
        recordChange()
        history.redo()?.let { restore(it) }
    }

    /** Records the current title and body into the history. Safe to call repeatedly for the same values. */
    fun recordChange() {
        val previous = history.current
        val next = takeSnapshot()
        if (next == previous) return
        val field = when {
            next.title.text != previous.title.text -> Field.TITLE
            next.body.text != previous.body.text -> Field.BODY
            else -> null
        }
        if (field == null) {
            // Only the cursor moved: undo should return it here, and typing at the new place is a new step.
            history.replaceCurrent(next)
            lastEdit = null
            return
        }
        val last = lastEdit
        val continuesTyping = last != null &&
                last.field == field &&
                last.time.elapsedNow() < TypingStepTimeout &&
                !startsNewWord(previous.valueOf(field), next.valueOf(field))
        if (continuesTyping) {
            history.replaceCurrent(next)
        } else {
            history.push(next)
        }
        lastEdit = LastEdit(field, TimeSource.Monotonic.markNow())
        updateFlags()
    }

    private fun restore(snapshot: Snapshot) {
        lastEdit = null
        title = snapshot.title
        body.value = snapshot.body
        updateFlags()
    }

    private fun updateFlags() {
        canUndo = history.canUndo
        canRedo = history.canRedo
    }

    /** Composition is IME-internal and must not split steps or be restored, so it's dropped. */
    private fun takeSnapshot(): Snapshot {
        return Snapshot(
                title = TextFieldValue(title.text, title.selection),
                body = TextFieldValue(body.value.text, body.value.selection),
        )
    }

    /** Typing a space or a new line after a word starts a new step, so undo goes word by word. */
    private fun startsNewWord(previous: TextFieldValue, next: TextFieldValue): Boolean {
        val typed = next.text.getOrNull(next.selection.start - 1)
        val before = previous.text.getOrNull(previous.selection.start - 1)
        return next.text.length > previous.text.length &&
                typed?.isWhitespace() == true &&
                before?.isWhitespace() != true
    }

    private enum class Field { TITLE, BODY }

    private data class LastEdit(val field: Field, val time: TimeSource.Monotonic.ValueTimeMark)

    private data class Snapshot(val title: TextFieldValue, val body: TextFieldValue) {

        fun valueOf(field: Field): TextFieldValue {
            return when (field) {
                Field.TITLE -> title
                Field.BODY -> body
            }
        }
    }
}

@Composable
internal fun rememberNoteEditorState(initialTitle: String, initialBody: String): NoteEditorState {
    val state = rememberSaveable(saver = NoteEditorState.Saver) {
        NoteEditorState(
                initialTitle = TextFieldValue(initialTitle),
                body = StyledTextState(TextFieldValue(initialBody)),
        )
    }
    LaunchedEffect(state) {
        snapshotFlow { state.title to state.body.value }
            .collect { state.recordChange() }
    }
    return state
}
