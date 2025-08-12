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
        // XML parts
        DsfTokenTypes.TAG_NAME                  -> arrayOf(XmlHighlighterColors.XML_TAG_NAME)
        DsfTokenTypes.ATTR_NAME, DsfTokenTypes.DSF_PARAM_NAME
            -> arrayOf(XmlHighlighterColors.XML_ATTRIBUTE_NAME)
        DsfTokenTypes.STRING                    -> arrayOf(XmlHighlighterColors.XML_ATTRIBUTE_VALUE)
        DsfTokenTypes.LT, DsfTokenTypes.GT,
        DsfTokenTypes.SLASH, DsfTokenTypes.EQ   -> arrayOf(XmlHighlighterColors.XML_TAG)
        DsfTokenTypes.COMMENT                   -> arrayOf(XmlHighlighterColors.XML_COMMENT)
        DsfTokenTypes.DOCTYPE, DsfTokenTypes.XML_DECL, DsfTokenTypes.PI
            -> arrayOf(DefaultLanguageHighlighterColors.METADATA)

        // DSF inside CDATA
        DsfTokenTypes.DSF_DIRECTIVE,            // :DPT...
        DsfTokenTypes.DSF_INLINE_CMD            // .dp..., .FOO
            -> arrayOf(DefaultLanguageHighlighterColors.KEYWORD)

        DsfTokenTypes.DSF_DOT                   -> arrayOf(DefaultLanguageHighlighterColors.KEYWORD)
        DsfTokenTypes.DSF_INLINE_VALUE,
        DsfTokenTypes.DSF_UNQUOTED              -> arrayOf(XmlHighlighterColors.XML_ATTRIBUTE_VALUE)

        // CDATA text that is not DSF
        DsfTokenTypes.CDATA_TEXT                -> TextAttributesKey.EMPTY_ARRAY

        else                                    -> TextAttributesKey.EMPTY_ARRAY
    }
}
