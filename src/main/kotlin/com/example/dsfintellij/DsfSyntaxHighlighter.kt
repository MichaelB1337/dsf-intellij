package com.example.dsfintellij

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class DsfSyntaxHighlighter : com.intellij.openapi.fileTypes.SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = DsfLexerAdapter()

    private fun key(name: String, base: TextAttributesKey) =
        TextAttributesKey.createTextAttributesKey(name, base)

    // Reuse XML palette so themes color it like real XML
    private val TAG_NAME  = key("DSF_TAG_NAME",  XmlHighlighterColors.XML_TAG_NAME)
    private val ATTR_NAME = key("DSF_ATTR_NAME", XmlHighlighterColors.XML_ATTRIBUTE_NAME)
    private val STR       = key("DSF_STRING",    XmlHighlighterColors.XML_ATTRIBUTE_VALUE)
    private val COMM      = key("DSF_COMMENT",   XmlHighlighterColors.XML_COMMENT)
    private val PUNC      = key("DSF_PUNC",      XmlHighlighterColors.XML_TAG) // <, >, /, =

    // Fallback for anything else
    private val TEXT      = key("DSF_TEXT", DefaultLanguageHighlighterColors.IDENTIFIER)

    override fun getTokenHighlights(t: IElementType): Array<TextAttributesKey> = when (t) {
        DsfTokenTypes.TAG_NAME    -> arrayOf(TAG_NAME)
        DsfTokenTypes.ATTR_NAME   -> arrayOf(ATTR_NAME)
        DsfTokenTypes.STRING      -> arrayOf(STR)
        DsfTokenTypes.COMMENT     -> arrayOf(COMM)
        DsfTokenTypes.LT,
        DsfTokenTypes.GT,
        DsfTokenTypes.SLASH,
        DsfTokenTypes.EQ          -> arrayOf(PUNC)
        DsfTokenTypes.WHITE_SPACE -> emptyArray()
        else                      -> arrayOf(TEXT)
    }
}
