package com.aspoliakov.securenotes.core_markdown

/**
 * Project SecureNotes
 */

enum class MdStyle {
    STRONG,
    EMPHASIS,
    STRIKETHROUGH,
    CODE_SPAN,
    CODE_BLOCK,
    QUOTE,
    HEADING_1,
    HEADING_2,
    HEADING_3,
    LINK_TEXT,
    LINK_URL,
    LIST_MARKER,
    TABLE_HEADER,

    /** Syntax characters (`**`, `#`, `>`, `|`, fences, ...). Dimmed in editor, hidden in preview. */
    MARKER,

    /** `|` between table cells. Dimmed in editor, replaced with a separator in preview. */
    TABLE_PIPE,

    /** Whole table delimiter row (`|---|---|`). Hidden in preview. */
    TABLE_DELIMITER,
}

/** Styled range of the source text, offsets are [start, end). */
data class MdSpan(
        val start: Int,
        val end: Int,
        val style: MdStyle,
)

/** Tappable link range of the source text, offsets are [start, end). */
data class MdLink(
        val start: Int,
        val end: Int,
        val url: String,
)

enum class MdInlineType(val marker: String) {
    STRONG("**"),
    EMPHASIS("*"),
    STRIKETHROUGH("~~"),
    CODE_SPAN("`"),
}

/**
 * Inline formatting node: [start, end) is the full node including markers,
 * [contentStart, contentEnd) is the text between opening and closing markers.
 */
data class MdInline(
        val type: MdInlineType,
        val start: Int,
        val end: Int,
        val contentStart: Int,
        val contentEnd: Int,
)

/** Fenced code block, offsets are [start, end) of the whole block including fences. */
data class MdCodeBlock(
        val start: Int,
        val end: Int,
        val fenced: Boolean,
)

class MdDocument(
        val text: String,
        val spans: List<MdSpan>,
        val links: List<MdLink>,
        val inlines: List<MdInline>,
        val codeBlocks: List<MdCodeBlock>,
) {

    companion object {
        fun empty(text: String): MdDocument {
            return MdDocument(text, emptyList(), emptyList(), emptyList(), emptyList())
        }
    }

    fun linkAt(offset: Int): MdLink? {
        return links.firstOrNull { offset >= it.start && offset < it.end }
    }

    fun isInCodeBlock(offset: Int): Boolean {
        return codeBlocks.any { offset > it.start && offset < it.end }
    }
}
