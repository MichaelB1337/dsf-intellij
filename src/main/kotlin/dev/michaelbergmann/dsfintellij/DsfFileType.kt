package dev.michaelbergmann.dsfintellij

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class DsfFileType private constructor() : LanguageFileType(DsfLanguage) {

    override fun getName() = "DSF File"
    override fun getDescription() = "DSF input file"
    override fun getDefaultExtension() = "dsf"
    override fun getIcon(): Icon = DsfIcons.FILE

    companion object {
        val INSTANCE = DsfFileType()
    }
}