package com.aspoliakov.securenotes.feature_note.presentation.styled_text

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import com.aspoliakov.securenotes.core_markdown.MarkdownEditing
import com.aspoliakov.securenotes.core_markdown.MdInlineType
import com.aspoliakov.securenotes.core_markdown.MdLinePrefix
import com.aspoliakov.securenotes.core_ui.Icons
import com.aspoliakov.securenotes.core_ui.resources.*
import org.jetbrains.compose.resources.StringResource

/**
 * Project SecureNotes
 *
 * Text styles available in [StyledTextEditor]; each one is applied by the markdown engine.
 */

internal enum class TextStyleAction(
        val icon: ImageVector,
        val label: StringResource,
        val apply: (TextFieldValue) -> TextFieldValue,
) {
    CLEAR(
            icon = Icons.TextStyleFormatClear,
            label = Res.string.feature_note_text_style_clear,
            apply = { MarkdownEditing.clearStyles(it) },
    ),
    BOLD(
            icon = Icons.TextStyleFormatBold,
            label = Res.string.feature_note_text_style_bold,
            apply = { MarkdownEditing.toggleInline(it, MdInlineType.STRONG) },
    ),
    ITALIC(
            icon = Icons.TextStyleFormatItalic,
            label = Res.string.feature_note_text_style_italic,
            apply = { MarkdownEditing.toggleInline(it, MdInlineType.EMPHASIS) },
    ),
    STRIKETHROUGH(
            icon = Icons.TextStyleFormatStrikethrough,
            label = Res.string.feature_note_text_style_strikethrough,
            apply = { MarkdownEditing.toggleInline(it, MdInlineType.STRIKETHROUGH) },
    ),
    HEADING(
            icon = Icons.TextStyleFormatHeading,
            label = Res.string.feature_note_text_style_heading,
            apply = { MarkdownEditing.cycleHeading(it) },
    ),
    BULLETED_LIST(
            icon = Icons.TextStyleFormatBulletedList,
            label = Res.string.feature_note_text_style_bulleted_list,
            apply = { MarkdownEditing.toggleLinePrefix(it, MdLinePrefix.BULLET) },
    ),
    NUMBERED_LIST(
            icon = Icons.TextStyleFormatNumberedList,
            label = Res.string.feature_note_text_style_numbered_list,
            apply = { MarkdownEditing.toggleLinePrefix(it, MdLinePrefix.NUMBERED) },
    ),
    QUOTE(
            icon = Icons.TextStyleFormatQuote,
            label = Res.string.feature_note_text_style_quote,
            apply = { MarkdownEditing.toggleLinePrefix(it, MdLinePrefix.QUOTE) },
    ),
    CODE(
            icon = Icons.TextStyleFormatCode,
            label = Res.string.feature_note_text_style_code,
            apply = { MarkdownEditing.toggleInline(it, MdInlineType.CODE_SPAN) },
    ),
    CODE_BLOCK(
            icon = Icons.TextStyleFormatCodeBlock,
            label = Res.string.feature_note_text_style_code_block,
            apply = { MarkdownEditing.toggleCodeBlock(it) },
    ),
    LINK(
            icon = Icons.TextStyleFormatLink,
            label = Res.string.feature_note_text_style_link,
            apply = { MarkdownEditing.insertLink(it) },
    ),
    TABLE(
            icon = Icons.TextStyleFormatTable,
            label = Res.string.feature_note_text_style_table,
            apply = { MarkdownEditing.insertTable(it) },
    ),
}
