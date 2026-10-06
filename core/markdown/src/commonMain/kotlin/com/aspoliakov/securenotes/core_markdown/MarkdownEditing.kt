package com.aspoliakov.securenotes.core_markdown

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Project SecureNotes
 *
 * Pure text operations behind the markdown toolbar and smart Enter.
 */

enum class MdLinePrefix {
    BULLET,
    NUMBERED,
    QUOTE,
}

object MarkdownEditing {

    private const val MAX_HEADING_LEVEL = 3
    private const val FENCE = "```"
    private const val TABLE_TEMPLATE = "|  |  |\n| --- | --- |\n|  |  |"
    private const val TABLE_FIRST_CELL_OFFSET = 2

    private val headingRegex = Regex("^(#{1,6}) ")
    private val bulletRegex = Regex("^(\\s*)([-*+]) ")
    private val numberedRegex = Regex("^(\\s*)(\\d{1,9})([.)]) ")
    private val quoteRegex = Regex("^((?:\\s*> ?)+)")
    private val urlRegex = Regex("^(https?://|www\\.)\\S+$")

    // region Inline: bold, italic, strikethrough, code

    /**
     * Unwraps formatting of [type] if the cursor/selection is inside it,
     * otherwise wraps the selection (line by line) or inserts an empty pair around the cursor.
     */
    fun toggleInline(value: TextFieldValue, type: MdInlineType): TextFieldValue {
        val text = value.text
        val selection = value.selection
        val marker = type.marker
        if (selection.collapsed && isInsideEmptyPair(text, selection.start, marker)) {
            val pos = selection.start
            return TextFieldValue(
                    text = text.removeRange(pos - marker.length, pos + marker.length),
                    selection = TextRange(pos - marker.length),
            )
        }
        val nodes = MdParser.parse(text).inlines.filter { node ->
            node.type == type && if (selection.collapsed) {
                selection.start > node.start && selection.start < node.end
            } else {
                node.start < selection.max && selection.min < node.end
            }
        }
        return if (nodes.isNotEmpty()) unwrap(value, nodes) else wrap(value, marker)
    }

    private fun isInsideEmptyPair(text: String, pos: Int, marker: String): Boolean {
        val start = pos - marker.length
        val end = pos + marker.length
        if (start < 0 || end > text.length) return false
        // `**|**` must not be treated as an empty italic pair `*|*`.
        val markerChar = marker.first()
        return text.substring(start, pos) == marker &&
                text.substring(pos, end) == marker &&
                text.getOrNull(start - 1) != markerChar &&
                text.getOrNull(end) != markerChar
    }

    private fun unwrap(value: TextFieldValue, nodes: List<MdInline>): TextFieldValue {
        val edits = nodes.flatMap { node ->
            listOf(
                    Edit(node.start, node.contentStart, ""),
                    Edit(node.contentEnd, node.end, ""),
            )
        }
        return value.applyEdits(edits)
    }

    private fun wrap(value: TextFieldValue, marker: String): TextFieldValue {
        val selection = value.selection
        val segments = if (selection.collapsed) emptyList() else wrapSegments(value.text, selection)
        return if (segments.isEmpty()) {
            insertPair(value.text, selection.max, marker)
        } else {
            wrapSegments(value.text, segments, marker)
        }
    }

    private fun insertPair(text: String, pos: Int, marker: String): TextFieldValue {
        return TextFieldValue(
                text = text.substring(0, pos) + marker + marker + text.substring(pos),
                selection = TextRange(pos + marker.length),
        )
    }

    /** Inline formatting can't span lines and must not include surrounding spaces or line prefixes. */
    private fun wrapSegments(text: String, selection: TextRange): List<Pair<Int, Int>> {
        return lineSegments(text, selection.min, selection.max).mapNotNull { (start, end) ->
            val lineStart = lineStartOf(text, start)
            val prefixEnd = if (start == lineStart) lineStart + linePrefixLength(text, lineStart) else start
            var segmentStart = maxOf(start, prefixEnd)
            var segmentEnd = end
            while (segmentStart < segmentEnd && text[segmentStart].isWhitespace()) segmentStart++
            while (segmentEnd > segmentStart && text[segmentEnd - 1].isWhitespace()) segmentEnd--
            if (segmentStart < segmentEnd) segmentStart to segmentEnd else null
        }
    }

    private fun wrapSegments(text: String, segments: List<Pair<Int, Int>>, marker: String): TextFieldValue {
        val result = StringBuilder()
        var last = 0
        segments.forEach { (start, end) ->
            result.append(text, last, start).append(marker).append(text, start, end).append(marker)
            last = end
        }
        result.append(text, last, text.length)
        val added = segments.size * marker.length * 2
        return TextFieldValue(
                text = result.toString(),
                selection = TextRange(
                        segments.first().first + marker.length,
                        segments.last().second + added - marker.length,
                ),
        )
    }

    // endregion

    // region Line prefixes: headings, lists, quotes

    /** Cycles the heading level of the selected lines: none → H1 → H2 → H3 → none. */
    fun cycleHeading(value: TextFieldValue): TextFieldValue {
        val text = value.text
        val lines = selectedLines(text, value.selection)
        val currentLevel = headingRegex.find(text.substring(lines.first().first, lines.first().second))
            ?.groupValues?.get(1)?.length ?: 0
        val nextLevel = when (currentLevel) {
            in 0 until MAX_HEADING_LEVEL -> currentLevel + 1
            else -> 0
        }
        val newPrefix = if (nextLevel == 0) "" else "#".repeat(nextLevel) + " "
        val edits = lines.map { (start, end) ->
            val existing = headingRegex.find(text.substring(start, end))?.value?.length ?: 0
            Edit(start, start + existing, newPrefix)
        }
        return value.applyEdits(edits)
    }

    /** Toggles a list/quote prefix on every selected line; switching bullet ↔ numbered replaces the prefix. */
    fun toggleLinePrefix(value: TextFieldValue, prefix: MdLinePrefix): TextFieldValue {
        val text = value.text
        val lines = selectedLines(text, value.selection)
        val regex = prefix.regex()
        val allHavePrefix = lines.all { (start, end) -> regex.containsMatchIn(text.substring(start, end)) }
        val edits = lines.mapIndexed { index, (start, end) ->
            val line = text.substring(start, end)
            when {
                allHavePrefix -> {
                    val match = regex.find(line)!!
                    val indent = if (prefix == MdLinePrefix.QUOTE) "" else match.groupValues[1]
                    Edit(start, start + match.value.length, indent)
                }
                prefix == MdLinePrefix.QUOTE -> Edit(start, start, "> ")
                else -> {
                    val existing = bulletRegex.find(line) ?: numberedRegex.find(line)
                    val indent = existing?.groupValues?.get(1) ?: line.takeWhile { it == ' ' || it == '\t' }
                    val marker = if (prefix == MdLinePrefix.BULLET) "- " else "${index + 1}. "
                    Edit(start, start + (existing?.value?.length ?: indent.length), indent + marker)
                }
            }
        }
        return value.applyEdits(edits)
    }

    private fun MdLinePrefix.regex(): Regex {
        return when (this) {
            MdLinePrefix.BULLET -> bulletRegex
            MdLinePrefix.NUMBERED -> numberedRegex
            MdLinePrefix.QUOTE -> quoteRegex
        }
    }

    // endregion

    // region Blocks: code block, link, table

    /** Removes the fences if the cursor is inside a fenced code block, otherwise fences the selected lines. */
    fun toggleCodeBlock(value: TextFieldValue): TextFieldValue {
        val text = value.text
        val selection = value.selection
        val block = MdParser.parse(text).codeBlocks.firstOrNull {
            it.fenced && selection.min >= it.start && selection.max <= it.end
        }
        val lines = selectedLines(text, selection)
        val start = lines.first().first
        val end = lines.last().second
        val content = text.substring(start, end)
        val contentStart = start + FENCE.length + 1
        return when {
            block != null -> unfence(value, block)
            selection.collapsed && start == end -> TextFieldValue(
                    text = text.substring(0, start) + "$FENCE\n\n$FENCE" + text.substring(end),
                    selection = TextRange(contentStart),
            )
            else -> TextFieldValue(
                    text = text.substring(0, start) + "$FENCE\n$content\n$FENCE" + text.substring(end),
                    selection = TextRange(contentStart, contentStart + content.length),
            )
        }
    }

    private fun unfence(value: TextFieldValue, block: MdCodeBlock): TextFieldValue {
        return value.applyEdits(fenceRemovalEdits(value.text, block))
    }

    /** Removes the opening fence line and, if the block is closed, the closing fence line. */
    private fun fenceRemovalEdits(text: String, block: MdCodeBlock): List<Edit> {
        val openEnd = text.indexOf('\n', block.start).let { if (it == -1 || it >= block.end) block.end else it + 1 }
        val closeStart = lineStartOf(text, block.end - 1)
        val isClosed = closeStart >= openEnd && text.substring(closeStart, block.end).trim().startsWith(FENCE)
        val openEdit = Edit(block.start, openEnd, "")
        return if (isClosed) {
            listOf(openEdit, Edit((closeStart - 1).coerceAtLeast(openEnd), block.end, ""))
        } else {
            listOf(openEdit)
        }
    }

    /** `[selection](|)`, or `[|](url)` when a URL is selected, or `[|]()` without a selection. */
    fun insertLink(value: TextFieldValue): TextFieldValue {
        val text = value.text
        val selection = value.selection
        val selected = text.substring(selection.min, selection.max)
        val before = text.substring(0, selection.min)
        val after = text.substring(selection.max)
        return when {
            selected.isBlank() -> TextFieldValue(before + "[]()" + after, TextRange(selection.min + 1))
            urlRegex.matches(selected.trim()) -> TextFieldValue(
                    text = before + "[](${selected.trim()})" + after,
                    selection = TextRange(selection.min + 1),
            )
            else -> TextFieldValue(
                    text = before + "[$selected]()" + after,
                    selection = TextRange(selection.min + selected.length + 3),
            )
        }
    }

    /**
     * Inserts a 2×2 table on the current blank line or after the current line, cursor in the first cell.
     * The table is separated from surrounding text by blank lines, otherwise neighbour lines would merge into it.
     */
    fun insertTable(value: TextFieldValue): TextFieldValue {
        val text = value.text
        val pos = value.selection.max
        val lineStart = lineStartOf(text, pos)
        val lineEnd = lineEndOf(text, pos)
        val lineBlank = text.substring(lineStart, lineEnd).isBlank()
        val insertAt = if (lineBlank) lineStart else lineEnd
        val previousLineBlank = lineStart == 0 ||
                text.substring(lineStartOf(text, lineStart - 1), lineStart - 1).isBlank()
        val leading = when {
            !lineBlank -> "\n\n"
            !previousLineBlank -> "\n"
            else -> ""
        }
        val trailing = if (lineEnd < text.length) "\n" else ""
        val newText = text.substring(0, insertAt) + leading + TABLE_TEMPLATE + trailing + text.substring(lineEnd)
        return TextFieldValue(newText, TextRange(insertAt + leading.length + TABLE_FIRST_CELL_OFFSET))
    }

    // endregion

    // region Clear styles

    /**
     * Removes styles from the selection, or from the current line without a selection: inline formatting,
     * headings, lists, quotes and code fences. Links and tables are content rather than styles and are kept.
     */
    fun clearStyles(value: TextFieldValue): TextFieldValue {
        val text = value.text
        val selection = value.selection
        val lines = selectedLines(text, selection)
        val start = if (selection.collapsed) lines.first().first else selection.min
        val end = if (selection.collapsed) lines.last().second else selection.max
        val document = MdParser.parse(text)
        val inlineEdits = document.inlines
            .filter { it.start < end && start < it.end }
            .flatMap { listOf(Edit(it.start, it.contentStart, ""), Edit(it.contentEnd, it.end, "")) }
        val fenceEdits = document.codeBlocks
            .filter { it.fenced && it.start < end && start < it.end }
            .flatMap { fenceRemovalEdits(text, it) }
        val prefixEdits = lines
            .filterNot { (lineStart, _) -> document.isInCodeBlock(lineStart) }
            .map { (lineStart, _) -> Edit(lineStart, lineStart + linePrefixLength(text, lineStart), "") }
        return value.applyEdits(inlineEdits + fenceEdits + prefixEdits)
    }

    // endregion

    // region Smart Enter

    /**
     * Called with the field value before and after a change. If the change is a single line break typed
     * inside a list item or quote, continues the prefix on the new line, or ends the list on an empty item.
     */
    fun onNewline(old: TextFieldValue, new: TextFieldValue): TextFieldValue {
        val pos = old.selection.start
        val isTypedNewline = old.selection.collapsed &&
                new.selection.collapsed &&
                new.selection.start == pos + 1 &&
                new.text.length == old.text.length + 1 &&
                new.text[pos] == '\n' &&
                new.text.startsWith(old.text.substring(0, pos)) &&
                new.text.endsWith(old.text.substring(pos))
        return (if (isTypedNewline) continueLine(old.text, pos) else null) ?: new
    }

    /** Line break at [pos] of [text] with the line prefix handled, or null if the line has no prefix to handle. */
    private fun continueLine(text: String, pos: Int): TextFieldValue? {
        val lineStart = lineStartOf(text, pos)
        val lineEnd = lineEndOf(text, pos)
        val line = text.substring(lineStart, lineEnd)
        val (prefix, continuation) = continuationFor(line)
            ?.takeIf { (prefix, _) -> pos - lineStart >= prefix.length && !MdParser.parse(text).isInCodeBlock(pos) }
            ?: return null
        val inserted = "\n" + continuation
        return if (line.substring(prefix.length).isBlank()) {
            // Enter on an empty item ends the list: drop the prefix and stay on this line.
            TextFieldValue(
                    text = text.substring(0, lineStart) + text.substring(lineEnd),
                    selection = TextRange(lineStart),
            )
        } else {
            TextFieldValue(
                    text = text.substring(0, pos) + inserted + text.substring(pos),
                    selection = TextRange(pos + inserted.length),
            )
        }
    }

    /** @return the line's prefix and the prefix to put on the next line, or null if the line has none. */
    private fun continuationFor(line: String): Pair<String, String>? {
        val bullet = bulletRegex.find(line)
        val numbered = numberedRegex.find(line)
        val quote = quoteRegex.find(line)
        return when {
            // Task items `- [ ]` are not supported yet.
            bullet != null -> bullet.takeUnless { line.substring(it.value.length).startsWith("[") }
                ?.let { it.value to "${it.groupValues[1]}${it.groupValues[2]} " }
            numbered != null -> {
                val next = numbered.groupValues[2].toLong() + 1
                numbered.value to "${numbered.groupValues[1]}$next${numbered.groupValues[3]} "
            }
            quote != null -> quote.value to if (quote.value.endsWith(" ")) quote.value else "${quote.value} "
            else -> null
        }
    }

    // endregion

    // region Helpers

    private fun lineStartOf(text: String, pos: Int): Int {
        return if (pos <= 0) 0 else text.lastIndexOf('\n', pos - 1) + 1
    }

    private fun lineEndOf(text: String, pos: Int): Int {
        val newline = text.indexOf('\n', pos)
        return if (newline == -1) text.length else newline
    }

    /** Line ranges [start, end) touched by the selection; blank lines are skipped for multi-line selections. */
    private fun selectedLines(text: String, selection: TextRange): List<Pair<Int, Int>> {
        val firstStart = lineStartOf(text, selection.min)
        // A selection ending right at a line start doesn't include that line.
        val endsAtLineStart = !selection.collapsed &&
                selection.max > firstStart &&
                text.getOrNull(selection.max - 1) == '\n'
        val lastPos = if (endsAtLineStart) selection.max - 1 else selection.max
        val lines = mutableListOf<Pair<Int, Int>>()
        var start = firstStart
        while (true) {
            val end = lineEndOf(text, start)
            lines += start to end
            if (end >= lastPos || end >= text.length) break
            start = end + 1
        }
        val nonBlank = lines.filter { (start, end) -> text.substring(start, end).isNotBlank() }
        return nonBlank.ifEmpty { listOf(lines.first()) }
    }

    /** Splits [start, end) into per-line segments. */
    private fun lineSegments(text: String, start: Int, end: Int): List<Pair<Int, Int>> {
        val segments = mutableListOf<Pair<Int, Int>>()
        var segmentStart = start
        while (segmentStart < end) {
            val newline = text.indexOf('\n', segmentStart).let { if (it == -1 || it > end) end else it }
            segments += segmentStart to newline
            segmentStart = newline + 1
        }
        return segments
    }

    private fun linePrefixLength(text: String, lineStart: Int): Int {
        val line = text.substring(lineStart, lineEndOf(text, lineStart))
        var length = quoteRegex.find(line)?.value?.length ?: 0
        val rest = line.substring(length)
        length += (headingRegex.find(rest) ?: bulletRegex.find(rest) ?: numberedRegex.find(rest))?.value?.length ?: 0
        return length
    }

    /** Non-overlapping replacement of [start, end) with [text]. */
    private data class Edit(val start: Int, val end: Int, val text: String)

    /**
     * Applies [edits] and maps the selection: offsets inside a replaced range move to its new end,
     * offsets at an insertion point move after the inserted text.
     */
    private fun TextFieldValue.applyEdits(edits: List<Edit>): TextFieldValue {
        val sorted = edits.filter { it.start != it.end || it.text.isNotEmpty() }.sortedBy { it.start }
        val result = StringBuilder()
        var last = 0
        sorted.forEach { edit ->
            result.append(text, last, edit.start).append(edit.text)
            last = edit.end
        }
        result.append(text, last, text.length)

        fun map(pos: Int): Int {
            var shift = 0
            for (edit in sorted) {
                when {
                    pos < edit.start -> break
                    pos >= edit.end -> shift += edit.text.length - (edit.end - edit.start)
                    else -> return edit.start + shift + edit.text.length
                }
            }
            return pos + shift
        }

        return TextFieldValue(
                text = result.toString(),
                selection = TextRange(map(selection.start), map(selection.end)),
        )
    }

    // endregion
}
