package dev.michaelbergmann.dsfintellij

import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class DsfSyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(p: Project?, f: VirtualFile?) = DsfSyntaxHighlighter()
}