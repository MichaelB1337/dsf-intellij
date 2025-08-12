package dev.michaelbergmann.dsfintellij

import com.intellij.lexer.FlexAdapter

class DsfLexerAdapter : FlexAdapter(DsfLexer(null))