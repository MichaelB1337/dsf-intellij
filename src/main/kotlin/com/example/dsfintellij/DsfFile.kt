package com.example.dsfintellij

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class DsfFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, DsfLanguage) {
    override fun getFileType(): FileType = DsfFileType.INSTANCE
    override fun toString(): String = "DSF File"
}