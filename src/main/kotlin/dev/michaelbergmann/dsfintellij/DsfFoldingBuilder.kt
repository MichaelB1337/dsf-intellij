package dev.michaelbergmann.dsfintellij

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import kotlin.math.max
import kotlin.math.min

class DsfFoldingBuilder : FoldingBuilderEx(), DumbAware {

    // Newer API
    override fun buildFoldRegions(
        element: PsiElement,
        document: Document,
        quick: Boolean
    ): Array<FoldingDescriptor> = buildForElement(element, document)

    // Older API – delegate via PSI
    override fun buildFoldRegions(
        node: ASTNode,
        document: Document
    ): Array<FoldingDescriptor> = buildForElement(node.psi, document)

    private fun buildForElement(element: PsiElement, document: Document): Array<FoldingDescriptor> {
        val node = element.node
        val text = document.charsSequence
        val fileRange = node.textRange
        val startOffset = fileRange.startOffset
        val endOffset = fileRange.endOffset

        data class OpenTag(val name: String, val startOffset: Int)

        val descriptors = mutableListOf<FoldingDescriptor>()
        val stack = ArrayDeque<OpenTag>()

        var i = startOffset
        var inComment = false
        var inCdata = false
        var inPI = false

        fun startsWithAt(pos: Int, s: String): Boolean {
            if (pos + s.length > endOffset) return false
            for (k in s.indices) if (text[pos + k] != s[k]) return false
            return true
        }

        fun indexOfForward(pos: Int, ch: Char): Int {
            var p = pos
            while (p < endOffset) { if (text[p] == ch) return p; p++ }
            return -1
        }

        fun readName(pos: Int): Pair<String, Int>? {
            var p = pos
            if (p >= endOffset) return null
            val c0 = text[p]
            if (!isNameStartChar(c0)) return null
            p++
            while (p < endOffset && isNameChar(text[p])) p++
            val name = text.subSequence(pos, p).toString()
            return name to p
        }

        fun buildPlaceholderForOpenTag(openStart: Int, gt: Int): String {
            // Show "<tag ...> …"
            val headEnd = min(gt + 1, text.length)
            var head = text.subSequence(openStart, headEnd).toString()
            head = head.replace('\n', ' ').replace(Regex("\\s+"), " ").trim()
            if (head.length > 80) head = head.substring(0, 77) + "…"
            return "$head …"
        }

        while (i < endOffset) {
            if (inComment) {
                val close = indexOfSubseq(text, i, endOffset, "-->")
                if (close == -1) break
                i = close + 3; inComment = false; continue
            }
            if (inCdata) {
                val close = indexOfSubseq(text, i, endOffset, "]]>")
                if (close == -1) break
                i = close + 3; inCdata = false; continue
            }
            if (inPI) {
                val close = indexOfSubseq(text, i, endOffset, "?>")
                if (close == -1) break
                i = close + 2; inPI = false; continue
            }

            val ch = text[i]
            if (ch == '<') {
                when {
                    startsWithAt(i, "<!--") -> { inComment = true; i += 4; continue }
                    startsWithAt(i, "<![CDATA[") -> { inCdata = true; i += 9; continue }
                    startsWithAt(i, "<?") -> { inPI = true; i += 2; continue }
                    startsWithAt(i, "</") -> {
                        val nameStart = i + 2
                        val namePair = readName(nameStart)
                        if (namePair != null) {
                            val (name, after) = namePair
                            val gt = indexOfForward(after, '>')
                            if (gt != -1) {
                                val idx = stack.indexOfLast { it.name == name }
                                if (idx != -1) {
                                    val opener = stack.removeAt(idx)
                                    if (hasNewline(text, opener.startOffset, gt + 1)) {
                                        val openGt = indexOfForward(opener.startOffset + 1, '>').let { if (it == -1) gt else it }
                                        val placeholder = buildPlaceholderForOpenTag(opener.startOffset, openGt)
                                        // Use the PsiElement-based ctor to set placeholder text
                                        descriptors += FoldingDescriptor(
                                            element,
                                            opener.startOffset,
                                            gt + 1,
                                            /* group = */ null,
                                            /* placeholderText = */ placeholder
                                        )
                                    }
                                }
                                i = gt + 1; continue
                            }
                        }
                    }
                    else -> {
                        val nameStart = i + 1
                        val namePair = readName(nameStart)
                        if (namePair != null) {
                            val (_, afterName) = namePair
                            val gt = indexOfForward(afterName, '>')
                            if (gt != -1) {
                                val selfClosing = gt - 1 >= i && text[gt - 1] == '/'
                                if (!selfClosing) {
                                    val tagName = text.subSequence(nameStart, afterName).toString()
                                    stack.addLast(OpenTag(tagName, i))
                                }
                                i = gt + 1; continue
                            }
                        }
                    }
                }
            }
            i++
        }

        return descriptors.toTypedArray()
    }

    // Return null so IDEA uses per-descriptor placeholders
    override fun getPlaceholderText(node: ASTNode): String? = null
    override fun isCollapsedByDefault(node: ASTNode): Boolean = false

    // helpers
    private fun isNameStartChar(c: Char): Boolean = c == ':' || c == '_' || c.isLetter()
    private fun isNameChar(c: Char): Boolean = isNameStartChar(c) || c.isDigit() || c == '-' || c == '.'

    private fun indexOfSubseq(seq: CharSequence, from: Int, to: Int, needle: String): Int {
        if (needle.isEmpty()) return from
        val lastStart = to - needle.length
        var i = max(from, 0)
        while (i <= lastStart) {
            var k = 0
            while (k < needle.length && seq[i + k] == needle[k]) k++
            if (k == needle.length) return i
            i++
        }
        return -1
    }

    private fun hasNewline(seq: CharSequence, start: Int, end: Int): Boolean {
        var i = start
        while (i < end && i < seq.length) {
            val ch = seq[i]
            if (ch == '\n' || ch == '\r') return true
            i++
        }
        return false
    }
}