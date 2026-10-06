package com.aspoliakov.securenotes.feature_notes_browser.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.aspoliakov.securenotes.core_markdown.MarkdownPreviewRenderer
import com.aspoliakov.securenotes.core_markdown.rememberMarkdownStyles

/**
 * Project SecureNotes
 *
 * Styled note text for previews: styles applied, markdown syntax hidden.
 */

@Composable
internal fun rememberStyledPreviewText(text: String, style: TextStyle): AnnotatedString {
    val markdownStyles = rememberMarkdownStyles(
            baseStyle = style,
            headingScales = listOf(1.2F, 1.1F, 1F),
    )
    return remember(text, markdownStyles) { MarkdownPreviewRenderer.render(text, markdownStyles) }
}
