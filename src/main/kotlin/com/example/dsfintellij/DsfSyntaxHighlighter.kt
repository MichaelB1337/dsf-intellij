package com.example.dsfintellij

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.XmlHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class DsfSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = DsfLexerAdapter()

    private fun key(name: String, base: TextAttributesKey) =
        TextAttributesKey.createTextAttributesKey(name, base)

    // Reuse IntelliJ’s XML colors so themes do the right thing
    private val TAG_NAME   = key("DSF_TAG_NAME",   XmlHighlighterColors.XML_TAG_NAME)
    private val ATTR_NAME  = key("DSF_ATTR_NAME",  XmlHighlighterColors.XML_ATTRIBUTE_NAME)
    private val ATTR_VALUE = key("DSF_ATTR_VALUE", XmlHighlighterColors.XML_ATTRIBUTE_VALUE)
    private val COMMENT    = key("DSF_COMMENT",    XmlHighlighterColors.XML_COMMENT)
    private val PUNCT      = key("DSF_PUNCT",      XmlHighlighterColors.XML_TAG)          // <, >, /
    private val META       = key("DSF_META",       DefaultLanguageHighlighterColors.METADATA)

    // DSF-specific
    private val DSF_KEYWORD = key("DSF_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD) // :DIRECTIVE, .dpfoo, .bar
    private val DSF_VALUE   = key("DSF_VALUE",   DefaultLanguageHighlighterColors.STRING)  // inline args, unquoted bits

    override fun getTokenHighlights(t: IElementType): Array<TextAttributesKey> = when (t) {
        // XML bits
        DsfTokenTypes.TAG_NAME                 -> arrayOf(TAG_NAME)
        DsfTokenTypes.ATTR_NAME               -> arrayOf(ATTR_NAME)
        DsfTokenTypes.STRING                  -> arrayOf(ATTR_VALUE)
        DsfTokenTypes.COMMENT                 -> arrayOf(COMMENT)
        DsfTokenTypes.LT, DsfTokenTypes.GT,
        DsfTokenTypes.SLASH, DsfTokenTypes.EQ -> arrayOf(PUNCT)
        DsfTokenTypes.DOCTYPE,
        DsfTokenTypes.XML_DECL,
        DsfTokenTypes.PI                      -> arrayOf(META)

        // CDATA content
        DsfTokenTypes.CDATA_TEXT              -> arrayOf(ATTR_VALUE)

        // DSF bits
        DsfTokenTypes.DSF_DIRECTIVE,
        DsfTokenTypes.DSF_INLINE_CMD          -> arrayOf(DSF_KEYWORD)
        DsfTokenTypes.DSF_PARAM_NAME          -> arrayOf(ATTR_NAME)
        DsfTokenTypes.DSF_UNQUOTED,
        DsfTokenTypes.DSF_INLINE_VALUE        -> arrayOf(DSF_VALUE)
        DsfTokenTypes.DSF_DOT                 -> arrayOf(PUNCT)

        // everything else (including WHITE_SPACE)
        else -> TextAttributesKey.EMPTY_ARRAY
    }
}
