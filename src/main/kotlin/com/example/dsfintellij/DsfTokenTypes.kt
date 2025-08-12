package com.example.dsfintellij

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType


object DsfTokenTypes {
    @JvmField val LT: IElementType = DsfTokenType("LT")
    @JvmField val GT: IElementType = DsfTokenType("GT")
    @JvmField val SLASH: IElementType = DsfTokenType("SLASH")
    @JvmField val EQ: IElementType = DsfTokenType("EQ")
    @JvmField val ATTR_NAME: IElementType = DsfTokenType("ATTR_NAME")
    @JvmField val STRING: IElementType = DsfTokenType("STRING")

    @JvmField val COMMENT: IElementType = DsfTokenType("COMMENT")
    @JvmField val XML_DECL: IElementType = DsfTokenType("XML_DECL")
    @JvmField val DOCTYPE: IElementType = DsfTokenType("DOCTYPE")
    @JvmField val PI: IElementType = DsfTokenType("PI")
    @JvmField val CDATA_TEXT: IElementType = DsfTokenType("CDATA_TEXT")

    @JvmField val DSF_DIRECTIVE: IElementType = DsfTokenType("DSF_DIRECTIVE")
    @JvmField val DSF_PARAM_NAME: IElementType = DsfTokenType("DSF_PARAM_NAME")
    @JvmField val DSF_DOT: IElementType = DsfTokenType("DSF_DOT")
    @JvmField val DSF_UNQUOTED: IElementType = DsfTokenType("DSF_UNQUOTED")
    @JvmField val DSF_INLINE_CMD: IElementType = DsfTokenType("DSF_INLINE_CMD")
    @JvmField val DSF_INLINE_VALUE: IElementType = DsfTokenType("DSF_INLINE_VALUE")
    @JvmField val TAG_NAME: IElementType = DsfTokenType("TAG_NAME")

    // expose a BAD_CHAR symbol for the lexer (map to platform token)
    @JvmField val BAD_CHAR: IElementType = TokenType.BAD_CHARACTER
}