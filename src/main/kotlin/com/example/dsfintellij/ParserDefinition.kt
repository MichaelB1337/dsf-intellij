// DsfParserDefinition.kt
package com.example.dsfintellij

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class DsfParserDefinition : ParserDefinition {

    companion object {
        val FILE = IFileElementType(DsfLanguage)
        private val COMMENTS = TokenSet.create(DsfTokenTypes.COMMENT)
        private val STRINGS = TokenSet.create(
            DsfTokenTypes.STRING,
            DsfTokenTypes.DSF_INLINE_VALUE,
            DsfTokenTypes.DSF_UNQUOTED,
            DsfTokenTypes.CDATA_TEXT
        )
        private val WHITESPACE = TokenSet.create(DsfTokenTypes.WHITE_SPACE)
    }

    override fun createLexer(project: Project?): Lexer = DsfLexerAdapter()

    // Minimal parser: consume all tokens, wrap them in a single file node, return the tree.
    override fun createParser(project: Project?): PsiParser = PsiParser { root, builder ->
        val mark = builder.mark()
        while (!builder.eof()) builder.advanceLexer()
        mark.done(root)                 // 'root' is the FILE element type passed in
        builder.treeBuilt               // return ASTNode
    }

    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = COMMENTS
    override fun getStringLiteralElements(): TokenSet = STRINGS
    override fun getWhitespaceTokens(): TokenSet = WHITESPACE

    override fun createElement(node: ASTNode): PsiElement =
        throw UnsupportedOperationException("No PSI elements")

    override fun createFile(viewProvider: FileViewProvider): PsiFile =
        DsfFile(viewProvider)
}