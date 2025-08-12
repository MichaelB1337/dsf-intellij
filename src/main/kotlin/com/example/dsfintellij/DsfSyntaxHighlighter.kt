package com.example.dsfintellij

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class DsfSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = DsfLexerAdapter()

    override fun getTokenHighlights(t: IElementType): Array<TextAttributesKey> {
        fun k(name: String, base: TextAttributesKey) =
            TextAttributesKey.createTextAttributesKey(name, base)

        val TAG = k("DSF_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG)
        val ATTR = k("DSF_ATTR", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE)
        val STR = k("DSF_STRING", DefaultLanguageHighlighterColors.STRING)
        val COMM = k("DSF_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        val META = k("DSF_META", DefaultLanguageHighlighterColors.METADATA)
        val KW = k("DSF_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val PUNC = k("DSF_PUNCT", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val TEXT = k("DSF_TEXT", DefaultLanguageHighlighterColors.IDENTIFIER)

        return when (t) {
            DsfTokenTypes.TAG_NAME -> arrayOf(TAG)
            DsfTokenTypes.ATTR_NAME, DsfTokenTypes.DSF_PARAM_NAME -> arrayOf(ATTR)
            DsfTokenTypes.STRING, DsfTokenTypes.DSF_INLINE_VALUE, DsfTokenTypes.CDATA_TEXT -> arrayOf(STR)
            DsfTokenTypes.COMMENT -> arrayOf(COMM)
            DsfTokenTypes.DOCTYPE, DsfTokenTypes.XML_DECL, DsfTokenTypes.PI -> arrayOf(META)
            DsfTokenTypes.DSF_DIRECTIVE, DsfTokenTypes.DSF_INLINE_CMD -> arrayOf(KW)
            DsfTokenTypes.LT, DsfTokenTypes.GT, DsfTokenTypes.SLASH, DsfTokenTypes.EQ, DsfTokenTypes.DSF_DOT -> arrayOf(PUNC)
            else -> arrayOf(TEXT)
        }
    }
}