package com.aspoliakov.securenotes.feature_note.presentation.component

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
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

    /** Range the editor has to scroll into view; reset by the editor once it is shown. */
    internal var revealRange by mutableStateOf<TextRange?>(null)

    /** Applies [action] to the current selection and keeps editing in the editor. */
    fun applyStyle(action: TextStyleAction) {
        value = action.apply(value)
        focusRequester.requestFocus()
    }

    /** Selects [range] without taking focus and scrolls it into view. */
    fun select(range: TextRange) {
        value = value.copy(selection = range)
        revealRange = range
    }
}
