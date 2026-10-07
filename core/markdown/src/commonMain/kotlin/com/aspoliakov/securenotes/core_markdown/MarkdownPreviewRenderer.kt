package com.aspoliakov.securenotes.core_markdown

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

/**
 * Project SecureNotes
 *
 * Renders markdown into styled text without syntax characters, e.g. for note previews.
 */

object MarkdownPreviewRenderer {

    private const val BULLET = '•'
    /** Cells keep their own padding (`| a | b |`), so the separator needs none. */
    private const val CELL_SEPARATOR = "·"

    fun render(text: String, styles: MarkdownStyles): AnnotatedString {
        val document = MdParser.parse(text)
        val hidden = BooleanArray(text.length)
        val replacements = mutableMapOf<Int, Replacement>()
        collectHiddenAndReplaced(document, hidden, replacements)
        hideEmptiedLines(text, hidden, replacements)

        // Build the output and remember where every source offset landed.
        val output = StringBuilder()
        val offsets = IntArray(text.length + 1)
        var i = 0
        while (i < text.length) {
            offsets[i] = output.length
            val replacement = replacements[i]
            if (replacement != null) {
                output.append(replacement.text)
                for (k in i + 1 until replacement.end) offsets[k] = output.length
                i = replacement.end
                continue
            }
            if (!hidden[i]) output.append(text[i])
            i++
        }
        offsets[text.length] = output.length

        val result = buildAnnotatedString {
            append(output)
            var lastParagraphEnd = -1
            document.spans.forEach { span ->
                val start = offsets[span.start]
                val end = offsets[span.end]
                if (end <= start) return@forEach
                styles.span(span.style)?.let { addStyle(it, start, end) }
                styles.paragraph(span.style)?.let { paragraphStyle ->
                    val range = lineRange(output, start, end)
                    if (range.first > lastParagraphEnd && !range.isEmpty()) {
                        addStyle(paragraphStyle, range.first, range.last + 1)
                        lastParagraphEnd = range.last + 1
                    }
                }
            }
        }
        val first = output.indexOfFirst { !it.isWhitespace() }
        val last = output.indexOfLast { !it.isWhitespace() }
        return if (first == -1) AnnotatedString("") else result.subSequence(first, last + 1)
    }

    private fun collectHiddenAndReplaced(
            document: MdDocument,
            hidden: BooleanArray,
            replacements: MutableMap<Int, Replacement>,
    ) {
        val text = document.text
        document.spans.sortedBy { it.start }.forEach { span ->
            when (span.style) {
                MdStyle.MARKER -> {
                    hidden.fill(true, span.start, span.end)
                    // `# Title`, `> quote`: drop the space after a line-leading marker too.
                    if (span.end < text.length && text[span.end] == ' ' && isLineStart(text, hidden, span.start)) {
                        hidden[span.end] = true
                    }
                }
                MdStyle.LINK_URL, MdStyle.TABLE_DELIMITER -> hidden.fill(true, span.start, span.end)
                MdStyle.TABLE_PIPE -> when {
                    isLineStart(text, hidden, span.start) -> {
                        // Also drop the first cell's padding, so rows start at the line start.
                        var end = span.end
                        while (end < text.length && text[end] != '\n' && text[end].isWhitespace()) end++
                        hidden.fill(true, span.start, end)
                    }
                    isLineEnd(text, span.end) -> hidden.fill(true, span.start, span.end)
                    else -> replacements[span.start] = Replacement(span.end, CELL_SEPARATOR)
                }
                MdStyle.LIST_MARKER -> {
                    val marker = text.substring(span.start, span.end)
                    val bulletIndex = marker.indexOfFirst { it == '-' || it == '*' || it == '+' }
                    if (bulletIndex != -1) {
                        val replaced = marker.replaceRange(bulletIndex, bulletIndex + 1, BULLET.toString())
                        replacements[span.start] = Replacement(span.end, replaced)
                    }
                }
                else -> Unit
            }
        }
    }

    /** Lines that had content but consist only of hidden syntax (fences, table delimiter) are removed entirely. */
    private fun hideEmptiedLines(text: String, hidden: BooleanArray, replacements: Map<Int, Replacement>) {
        var lineStart = 0
        while (lineStart <= text.length) {
            val newline = text.indexOf('\n', lineStart)
            val lineEnd = if (newline == -1) text.length else newline
            var hadContent = false
            var keptContent = false
            for (k in lineStart until lineEnd) {
                if (text[k].isWhitespace()) continue
                hadContent = true
                if (!hidden[k] || replacements.containsKey(k)) keptContent = true
            }
            if (hadContent && !keptContent) {
                hidden.fill(true, lineStart, lineEnd)
                if (newline != -1) hidden[newline] = true
            }
            if (newline == -1) break
            lineStart = newline + 1
        }
    }

    /** Whether only hidden characters or whitespace precede [offset] on its line. */
    private fun isLineStart(text: String, hidden: BooleanArray, offset: Int): Boolean {
        var k = offset - 1
        while (k >= 0 && text[k] != '\n' && (hidden[k] || text[k].isWhitespace())) k--
        return k < 0 || text[k] == '\n'
    }

    /** Whether only whitespace follows [offset] on its line. */
    private fun isLineEnd(text: String, offset: Int): Boolean {
        var k = offset
        while (k < text.length && text[k] != '\n' && text[k].isWhitespace()) k++
        return k == text.length || text[k] == '\n'
    }

    private data class Replacement(val end: Int, val text: String)
}
