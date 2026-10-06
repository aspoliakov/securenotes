package com.aspoliakov.securenotes.core_markdown

import io.github.aakira.napier.Napier
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.CancellationToken
import org.intellij.markdown.parser.MarkdownParser

/**
 * Project SecureNotes
 *
 * Parses GFM markdown into a flat [MdDocument] whose offsets refer to the source text.
 */

object MdParser {

    private val parser by lazy {
        MarkdownParser(
                flavour = GFMFlavourDescriptor(),
                assertionsEnabled = false,
                cancellationToken = CancellationToken.NonCancellable,
        )
    }

    fun parse(text: String): MdDocument {
        val root = if (text.isBlank()) null else buildTree(text)
        return if (root == null) MdDocument.empty(text) else MdCollector(text).apply { visit(root) }.build()
    }

    private fun buildTree(text: String): ASTNode? {
        val source: CharSequence = text
        return runCatching { parser.buildMarkdownTreeFromString(source) }
            .onFailure { Napier.e("Markdown parsing failed", it) }
            .getOrNull()
    }
}

private class MdCollector(private val text: String) {

    private val spans = mutableListOf<MdSpan>()
    private val links = mutableListOf<MdLink>()
    private val inlines = mutableListOf<MdInline>()
    private val codeBlocks = mutableListOf<MdCodeBlock>()

    fun build(): MdDocument {
        return MdDocument(
                text = text,
                spans = spans,
                links = links,
                inlines = inlines,
                codeBlocks = codeBlocks,
        )
    }

    @Suppress("CyclomaticComplexMethod")
    fun visit(node: ASTNode) {
        when (node.type) {
            MarkdownElementTypes.STRONG -> inline(node, MdInlineType.STRONG, MdStyle.STRONG, MarkdownTokenTypes.EMPH)
            MarkdownElementTypes.EMPH -> inline(node, MdInlineType.EMPHASIS, MdStyle.EMPHASIS, MarkdownTokenTypes.EMPH)
            GFMElementTypes.STRIKETHROUGH -> {
                inline(node, MdInlineType.STRIKETHROUGH, MdStyle.STRIKETHROUGH, GFMTokenTypes.TILDE)
            }
            MarkdownElementTypes.CODE_SPAN -> codeSpan(node)
            MarkdownElementTypes.CODE_FENCE -> codeFence(node)
            MarkdownElementTypes.CODE_BLOCK -> {
                span(node, MdStyle.CODE_BLOCK)
                codeBlocks += MdCodeBlock(node.startOffset, node.endOffset, fenced = false)
            }
            MarkdownElementTypes.BLOCK_QUOTE -> styled(node, MdStyle.QUOTE)
            MarkdownElementTypes.ATX_1, MarkdownElementTypes.SETEXT_1 -> styled(node, MdStyle.HEADING_1)
            MarkdownElementTypes.ATX_2, MarkdownElementTypes.SETEXT_2 -> styled(node, MdStyle.HEADING_2)
            MarkdownElementTypes.ATX_3,
            MarkdownElementTypes.ATX_4,
            MarkdownElementTypes.ATX_5,
            MarkdownElementTypes.ATX_6 -> styled(node, MdStyle.HEADING_3)
            MarkdownElementTypes.INLINE_LINK -> inlineLink(node)
            MarkdownElementTypes.AUTOLINK -> angleAutolink(node)
            GFMTokenTypes.GFM_AUTOLINK -> autolink(node, node.startOffset, node.endOffset, prefix = "")
            MarkdownTokenTypes.EMAIL_AUTOLINK -> autolink(node, node.startOffset, node.endOffset, prefix = "mailto:")
            MarkdownTokenTypes.LIST_BULLET, MarkdownTokenTypes.LIST_NUMBER -> span(node, MdStyle.LIST_MARKER)
            MarkdownTokenTypes.BLOCK_QUOTE,
            MarkdownTokenTypes.ATX_HEADER,
            MarkdownTokenTypes.SETEXT_1,
            MarkdownTokenTypes.SETEXT_2,
            MarkdownTokenTypes.HORIZONTAL_RULE -> span(node, MdStyle.MARKER)
            GFMElementTypes.TABLE -> table(node)
            else -> visitChildren(node)
        }
    }

    private fun visitChildren(node: ASTNode) {
        node.children.forEach { visit(it) }
    }

    private fun span(node: ASTNode, style: MdStyle) {
        span(node.startOffset, node.endOffset, style)
    }

    private fun span(start: Int, end: Int, style: MdStyle) {
        val safeStart = start.coerceIn(0, text.length)
        val safeEnd = end.coerceIn(safeStart, text.length)
        if (safeEnd > safeStart) spans += MdSpan(safeStart, safeEnd, style)
    }

    private fun styled(node: ASTNode, style: MdStyle) {
        span(node, style)
        visitChildren(node)
    }

    private fun inline(node: ASTNode, type: MdInlineType, style: MdStyle, markerType: IElementType) {
        span(node, style)
        val leading = node.children.takeWhile { it.type == markerType }
        val trailing = node.children.drop(leading.size).takeLastWhile { it.type == markerType }
        (leading + trailing).forEach { span(it, MdStyle.MARKER) }
        inlines += MdInline(
                type = type,
                start = node.startOffset,
                end = node.endOffset,
                contentStart = leading.lastOrNull()?.endOffset ?: node.startOffset,
                contentEnd = trailing.firstOrNull()?.startOffset ?: node.endOffset,
        )
        node.children.subList(leading.size, node.children.size - trailing.size).forEach { visit(it) }
    }

    private fun codeSpan(node: ASTNode) {
        span(node, MdStyle.CODE_SPAN)
        val leading = node.children.takeWhile { it.type == MarkdownTokenTypes.BACKTICK }
        val trailing = node.children.drop(leading.size).takeLastWhile { it.type == MarkdownTokenTypes.BACKTICK }
        (leading + trailing).forEach { span(it, MdStyle.MARKER) }
        inlines += MdInline(
                type = MdInlineType.CODE_SPAN,
                start = node.startOffset,
                end = node.endOffset,
                contentStart = leading.lastOrNull()?.endOffset ?: node.startOffset,
                contentEnd = trailing.firstOrNull()?.startOffset ?: node.endOffset,
        )
    }

    private fun codeFence(node: ASTNode) {
        span(node, MdStyle.CODE_BLOCK)
        codeBlocks += MdCodeBlock(node.startOffset, node.endOffset, fenced = true)
        node.children
            .filter {
                it.type == MarkdownTokenTypes.CODE_FENCE_START ||
                        it.type == MarkdownTokenTypes.CODE_FENCE_END ||
                        it.type == MarkdownTokenTypes.FENCE_LANG
            }
            .forEach { span(it, MdStyle.MARKER) }
    }

    private fun inlineLink(node: ASTNode) {
        val destination = node.children.firstOrNull { it.type == MarkdownElementTypes.LINK_DESTINATION }
        if (destination != null) {
            val url = text.substring(destination.startOffset, destination.endOffset).removeSurrounding("<", ">")
            if (url.isNotBlank()) {
                links += MdLink(node.startOffset, node.endOffset, normalizeUrl(url))
            }
        }
        node.children.forEach { child ->
            when (child.type) {
                MarkdownElementTypes.LINK_TEXT -> {
                    span(child, MdStyle.LINK_TEXT)
                    child.children.forEach { textChild ->
                        val isBracket = textChild.type == MarkdownTokenTypes.LBRACKET ||
                                textChild.type == MarkdownTokenTypes.RBRACKET
                        if (isBracket) span(textChild, MdStyle.MARKER) else visit(textChild)
                    }
                }
                MarkdownElementTypes.LINK_DESTINATION, MarkdownElementTypes.LINK_TITLE -> span(child, MdStyle.LINK_URL)
                else -> span(child, MdStyle.MARKER)
            }
        }
    }

    /** `<https://example.com>` */
    private fun angleAutolink(node: ASTNode) {
        val start = node.startOffset
        val end = node.endOffset
        if (end - start <= 2) return
        span(start, start + 1, MdStyle.MARKER)
        span(end - 1, end, MdStyle.MARKER)
        val isEmail = node.children.any { it.type == MarkdownTokenTypes.EMAIL_AUTOLINK }
        autolink(node, start + 1, end - 1, prefix = if (isEmail) "mailto:" else "")
    }

    private fun autolink(node: ASTNode, start: Int, end: Int, prefix: String) {
        span(start, end, MdStyle.LINK_TEXT)
        val url = text.substring(start, end)
        links += MdLink(node.startOffset, node.endOffset, if (prefix.isEmpty()) normalizeUrl(url) else prefix + url)
    }

    private fun table(node: ASTNode) {
        node.children.forEach { child ->
            when (child.type) {
                GFMTokenTypes.TABLE_SEPARATOR -> span(child, MdStyle.TABLE_DELIMITER)
                GFMElementTypes.HEADER -> {
                    span(child, MdStyle.TABLE_HEADER)
                    tableRow(child)
                }
                GFMElementTypes.ROW -> tableRow(child)
                else -> visit(child)
            }
        }
    }

    private fun tableRow(node: ASTNode) {
        node.children.forEach { child ->
            if (child.type == GFMTokenTypes.TABLE_SEPARATOR) span(child, MdStyle.TABLE_PIPE) else visit(child)
        }
    }
}

private val schemeRegex = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*:")

internal fun normalizeUrl(url: String): String {
    return if (schemeRegex.containsMatchIn(url)) url else "https://$url"
}
