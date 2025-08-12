package com.example.dsfintellij

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

object DsfTokenTypes {
    // XML-like
    val LT: IElementType = DsfTokenType("LT")
    val GT: IElementType = DsfTokenType("GT")
    val SLASH: IElementType = DsfTokenType("SLASH")
    val EQ: IElementType = DsfTokenType("EQ")
    val TAG_NAME: IElementType = DsfTokenType("TAG_NAME")
    val ATTR_NAME: IElementType = DsfTokenType("ATTR_NAME")
    val STRING: IElementType = DsfTokenType("STRING")
    val COMMENT: IElementType = DsfTokenType("COMMENT")
    val DOCTYPE: IElementType = DsfTokenType("DOCTYPE")
    val XML_DECL: IElementType = DsfTokenType("XML_DECL")
    val PI: IElementType = DsfTokenType("PI")
    val CDATA_TEXT: IElementType = DsfTokenType("CDATA_TEXT")

    // DSF
    val DSF_DIRECTIVE: IElementType = DsfTokenType("DSF_DIRECTIVE")   // :NAME
    val DSF_DOT: IElementType = DsfTokenType("DSF_DOT")               // trailing .
    val DSF_PARAM_NAME: IElementType = DsfTokenType("DSF_PARAM_NAME") // NAME in directive
    val DSF_UNQUOTED: IElementType = DsfTokenType("DSF_UNQUOTED")     // unquoted directive value
    val DSF_INLINE_CMD: IElementType = DsfTokenType("DSF_INLINE_CMD") // .dp... or .foo
    val DSF_INLINE_VALUE: IElementType = DsfTokenType("DSF_INLINE_VALUE")

    // misc
    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE
    val BAD_CHAR: IElementType = TokenType.BAD_CHARACTER
}