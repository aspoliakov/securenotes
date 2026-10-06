package com.aspoliakov.securenotes.feature_note.presentation.styled_text

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Project SecureNotes
 *
 * State of [StyledTextEditor]: text with cursor/selection and focus. Observe [text] to persist changes.
 */

@Stable
internal class StyledTextState(initialValue: TextFieldValue) {

    companion object {
        val Saver: Saver<StyledTextState, Any> = Saver(
                save = { with(TextFieldValue.Saver) { save(it.value) } },
                restore = { saved -> TextFieldValue.Saver.restore(saved)?.let { StyledTextState(it) } },
        )
    }

    internal var value by mutableStateOf(initialValue)

    val text: String get() = value.text

    var isFocused by mutableStateOf(false)
        internal set

    internal val focusRequester = FocusRequester()

    /** Applies [action] to the current selection and keeps editing in the editor. */
    fun applyStyle(action: TextStyleAction) {
        value = action.apply(value)
        focusRequester.requestFocus()
    }
}

@Composable
internal fun rememberStyledTextState(initialText: String): StyledTextState {
    return rememberSaveable(saver = StyledTextState.Saver) { StyledTextState(TextFieldValue(initialText)) }
}
