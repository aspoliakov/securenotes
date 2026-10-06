package com.aspoliakov.securenotes.feature_note.presentation.styled_text

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import com.aspoliakov.securenotes.core_markdown.*
import io.github.aakira.napier.Napier

/**
 * Project SecureNotes
 *
 * Text editor with styles, backed by Markdown: syntax stays visible and is styled in place; links open on tap.
 * Must not scroll internally (place it in a scrolling container), so pointer and text layout coordinates match.
 */

@Composable
internal fun StyledTextEditor(
        modifier: Modifier = Modifier,
        state: StyledTextState,
        textStyle: TextStyle,
        placeholder: String,
) {
    val value = state.value
    val styles = rememberMarkdownStyles(textStyle)
    val document = remember(value.text) { MdParser.parse(value.text) }
    val visualTransformation = remember(document, styles) { MarkdownVisualTransformation(document, styles) }
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val currentDocument by rememberUpdatedState(document)
    val uriHandler = LocalUriHandler.current
    BasicTextField(
            modifier = modifier
                .focusRequester(state.focusRequester)
                .onFocusChanged { state.isFocused = it.isFocused }
                .linkTaps(
                        layoutResult = { layoutResult },
                        document = { currentDocument },
                        onLinkClick = { url ->
                            runCatching { uriHandler.openUri(url) }
                                .onFailure { Napier.e("Can't open link", it) }
                        },
                ),
            value = value,
            onValueChange = { state.value = MarkdownEditing.onNewline(value, it) },
            textStyle = textStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            visualTransformation = visualTransformation,
            onTextLayout = { layoutResult = it },
            decorationBox = { innerTextField ->
                Box {
                    if (value.text.isEmpty()) {
                        Text(
                                text = placeholder,
                                style = textStyle.copy(color = textStyle.color.copy(alpha = 0.4F)),
                        )
                    }
                    innerTextField()
                }
            },
    )
}

/**
 * Opens a link on a short tap over its text. Observes events in the Initial pass and consumes only the final
 * up event of a link tap, so cursor placement, long-press selection and scrolling work as usual.
 */
private fun Modifier.linkTaps(
        layoutResult: () -> TextLayoutResult?,
        document: () -> MdDocument,
        onLinkClick: (String) -> Unit,
): Modifier {
    return pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            val url = findLink(layoutResult(), document(), down.position)
            val up = if (url != null) waitForUpOrCancellation(pass = PointerEventPass.Initial) else null
            val isTap = up != null &&
                    up.uptimeMillis - down.uptimeMillis < viewConfiguration.longPressTimeoutMillis &&
                    (up.position - down.position).getDistance() < viewConfiguration.touchSlop
            if (url != null && up != null && isTap) {
                up.consume()
                onLinkClick(url)
            }
        }
    }
}

private fun findLink(layout: TextLayoutResult?, document: MdDocument, position: Offset): String? {
    if (layout == null || document.links.isEmpty() || position.y !in 0F..layout.size.height.toFloat()) return null
    val offset = layout.getOffsetForPosition(position)
    val textLength = layout.layoutInput.text.length
    // The nearest caret offset may be on either side of the tapped character; a tap to the right of the
    // line end hits no character box at all and must place the cursor instead of opening the link.
    return listOf(offset, offset - 1).firstNotNullOfOrNull { candidate ->
        val hit = candidate in 0 until textLength && layout.getBoundingBox(candidate).contains(position)
        if (hit) document.linkAt(candidate)?.url else null
    }
}
