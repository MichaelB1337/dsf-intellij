// DsfTokenTypes.kt
package com.example.dsfintellij
import com.intellij.psi.tree.IElementType

object DsfTokenTypes {
    @JvmField val LT           = DsfTokenType("LT")            // "<"
    @JvmField val GT           = DsfTokenType("GT")            // ">"
    @JvmField val SLASH        = DsfTokenType("SLASH")         // "/"
    @JvmField val EQ           = DsfTokenType("EQ")            // "="
    @JvmField val TAG_NAME     = DsfTokenType("TAG_NAME")
    @JvmField val ATTR_NAME    = DsfTokenType("ATTR_NAME")
    @JvmField val STRING       = DsfTokenType("STRING")
    @JvmField val COMMENT      = DsfTokenType("COMMENT")
    @JvmField val WHITE_SPACE  = DsfTokenType("WHITE_SPACE")
    @JvmField val BAD_CHAR     = DsfTokenType("BAD_CHAR")
}