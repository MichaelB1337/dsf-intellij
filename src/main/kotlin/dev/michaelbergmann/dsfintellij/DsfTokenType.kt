package dev.michaelbergmann.dsfintellij

import com.intellij.psi.tree.IElementType
import com.intellij.lang.Language

class DsfTokenType(debugName: String) : IElementType(debugName, Language.findInstance(DsfLanguage::class.java))