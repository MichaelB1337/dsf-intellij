// DsfTokenTypes.kt
package com.example.dsfintellij
import com.intellij.psi.tree.IElementType


object DsfTokenTypes {
    // Punctuation
    @JvmField val LT            = DsfTokenType("LT")            // "<"
    @JvmField val GT            = DsfTokenType("GT")            // ">"
    @JvmField val SLASH         = DsfTokenType("SLASH")         // "/"
    @JvmField val EQ            = DsfTokenType("EQ")            // "="
    @JvmField val DSF_DOT       = DsfTokenType("DSF_DOT")       // "." that ends a directive

    // XML-ish
    @JvmField val TAG_NAME      = DsfTokenType("TAG_NAME")
    @JvmField val ATTR_NAME     = DsfTokenType("ATTR_NAME")
    @JvmField val STRING        = DsfTokenType("STRING")
    @JvmField val COMMENT       = DsfTokenType("COMMENT")
    @JvmField val DOCTYPE       = DsfTokenType("DOCTYPE")
    @JvmField val XML_DECL      = DsfTokenType("XML_DECL")
    @JvmField val PI            = DsfTokenType("PI")
    @JvmField val CDATA_TEXT    = DsfTokenType("CDATA_TEXT")

    // DSF
    @JvmField val DSF_DIRECTIVE   = DsfTokenType("DSF_DIRECTIVE")   // :DIRECTIVE
    @JvmField val DSF_PARAM_NAME  = DsfTokenType("DSF_PARAM_NAME")  // UPPER_PARAM in directive body
    @JvmField val DSF_UNQUOTED    = DsfTokenType("DSF_UNQUOTED")    // unquoted value inside directive
    @JvmField val DSF_INLINE_CMD  = DsfTokenType("DSF_INLINE_CMD")  // .dpfoo / .bar
    @JvmField val DSF_INLINE_VALUE= DsfTokenType("DSF_INLINE_VALUE")// inline args until EOL

    // Misc
    @JvmField val WHITE_SPACE   = DsfTokenType("WHITE_SPACE")
    @JvmField val BAD_CHAR      = DsfTokenType("BAD_CHAR")
}