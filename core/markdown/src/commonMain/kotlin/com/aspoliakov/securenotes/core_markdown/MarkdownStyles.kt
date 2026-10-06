package com.aspoliakov.securenotes.core_markdown

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified

/**
 * Project SecureNotes
 */

@Immutable
data class MarkdownStyles(
        private val spanStyles: Map<MdStyle, SpanStyle>,
        private val paragraphStyles: Map<MdStyle, ParagraphStyle>,
) {
    fun span(style: MdStyle): SpanStyle? {
        return spanStyles[style]
    }

    /** Applied to whole lines only, see [lineRange]. */
    fun paragraph(style: MdStyle): ParagraphStyle? {
        return paragraphStyles[style]
    }
}

/**
 * @param baseStyle text style of the surrounding text, heading sizes are derived from it.
 * @param headingScales font scale for H1, H2, H3.
 */
@Composable
fun rememberMarkdownStyles(
        baseStyle: TextStyle,
        headingScales: List<Float> = listOf(1.5F, 1.3F, 1.15F),
): MarkdownStyles {
    val colors = MaterialTheme.colorScheme
    val baseColor = baseStyle.color.takeIf { it != Color.Unspecified } ?: colors.onSurface
    return remember(colors, baseStyle, headingScales) {
        val marker = SpanStyle(color = baseColor.copy(alpha = 0.4F))
        val code = SpanStyle(fontFamily = FontFamily.Monospace, background = colors.surfaceVariant)
        val headings = listOf(MdStyle.HEADING_1, MdStyle.HEADING_2, MdStyle.HEADING_3).zip(headingScales)
        MarkdownStyles(
                spanStyles = mapOf(
                        MdStyle.STRONG to SpanStyle(fontWeight = FontWeight.Bold),
                        MdStyle.EMPHASIS to SpanStyle(fontStyle = FontStyle.Italic),
                        MdStyle.STRIKETHROUGH to SpanStyle(textDecoration = TextDecoration.LineThrough),
                        MdStyle.CODE_SPAN to code,
                        MdStyle.CODE_BLOCK to code,
                        MdStyle.QUOTE to SpanStyle(color = colors.onSurfaceVariant, fontStyle = FontStyle.Italic),
                        MdStyle.LINK_TEXT to SpanStyle(
                                color = colors.primary,
                                textDecoration = TextDecoration.Underline,
                        ),
                        MdStyle.LINK_URL to marker,
                        MdStyle.LIST_MARKER to SpanStyle(color = colors.primary, fontWeight = FontWeight.Bold),
                        MdStyle.TABLE_HEADER to SpanStyle(fontWeight = FontWeight.Bold),
                        MdStyle.MARKER to marker,
                        MdStyle.TABLE_PIPE to marker,
                        MdStyle.TABLE_DELIMITER to marker,
                ) + headings.associate { (style, scale) ->
                    style to SpanStyle(fontSize = baseStyle.fontSize * scale, fontWeight = FontWeight.Bold)
                },
                paragraphStyles = headings.associate { (style, scale) ->
                    style to ParagraphStyle(lineHeight = scaledLineHeight(baseStyle, scale))
                },
        )
    }
}

private fun scaledLineHeight(baseStyle: TextStyle, scale: Float): TextUnit {
    return if (baseStyle.lineHeight.isSpecified) baseStyle.lineHeight * scale else TextUnit.Unspecified
}

/** Expands [start, end) to the full lines it touches, without the trailing line break. */
internal fun lineRange(text: CharSequence, start: Int, end: Int): IntRange {
    val lineStart = if (start <= 0) 0 else text.lastIndexOf('\n', start - 1) + 1
    val searchFrom = if (end > start) end - 1 else start
    val newline = text.indexOf('\n', searchFrom)
    val lineEnd = if (newline == -1) text.length else newline
    return lineStart until lineEnd
}
