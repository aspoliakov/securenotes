package com.aspoliakov.securenotes.core_markdown

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Project SecureNotes
 *
 * Styles markdown source in place: the text itself is never changed, so cursor offsets map 1:1.
 */

object MarkdownHighlighter {

    fun highlight(document: MdDocument, styles: MarkdownStyles): AnnotatedString {
        return buildAnnotatedString {
            val text = document.text
            append(text)
            var lastParagraphEnd = -1
            document.spans.forEach { span ->
                styles.span(span.style)?.let { addStyle(it, span.start, span.end) }
                styles.paragraph(span.style)?.let { paragraphStyle ->
                    // Paragraph styles must cover whole lines, otherwise Compose breaks the line at the range edge.
                    val range = lineRange(text, span.start, span.end)
                    if (range.first > lastParagraphEnd && !range.isEmpty()) {
                        addStyle(paragraphStyle, range.first, range.last + 1)
                        lastParagraphEnd = range.last + 1
                    }
                }
            }
        }
    }
}

/**
 * @param document parsed text of the field; reparsed if the field text differs.
 */
class MarkdownVisualTransformation(
        private val document: MdDocument,
        private val styles: MarkdownStyles,
) : VisualTransformation {

    private var cachedText: String? = null
    private var cachedResult: AnnotatedString? = null

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val highlighted = cachedResult.takeIf { cachedText == raw } ?: run {
            val doc = if (document.text == raw) document else MdParser.parse(raw)
            MarkdownHighlighter.highlight(doc, styles).also {
                cachedText = raw
                cachedResult = it
            }
        }
        return TransformedText(highlighted, OffsetMapping.Identity)
    }

    override fun equals(other: Any?): Boolean {
        return other is MarkdownVisualTransformation && other.document === document && other.styles == styles
    }

    override fun hashCode(): Int {
        return 31 * document.hashCode() + styles.hashCode()
    }
}
