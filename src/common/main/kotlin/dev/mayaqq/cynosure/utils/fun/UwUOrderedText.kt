package dev.mayaqq.cynosure.utils.`fun`

import dev.mayaqq.cynosure.utils.`fun`.UwUfyBase.PHRASES
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.FormattedCharSink

public class UwUOrderedText(private val wrapped: FormattedCharSequence) : FormattedCharSequence {

    private val uwufiedStrings = buildList<Pair<Style, String>> {
        var current: Pair<Style, StringBuilder>? = null
        wrapped.accept { index, style, codePoint ->
            if (current == null) current = style to StringBuilder()
            if (current!!.first != style) {
                add(current!!.first to current!!.second.toString())
                current = style to StringBuilder()
            }
            current.second.appendCodePoint(codePoint)
            return@accept true
        }
        if (current != null) add(current.first to current.second.toString())
    }.uwufy()

    private fun List<Pair<Style, String>>.uwufy(): List<Pair<Style, String>> {
        val length = this.fold(0) { acc, pair -> acc + pair.second.length }
        val newList = this.map { pair ->
            pair.first to if (pair.first.font == Style.DEFAULT_FONT) {
                UwUfyBase.uwufyText(pair.second)
                    .let {
                        if (length % 3 == 0) {
                            return@let it.uppercase()
                        }
                        it
                    }
                    .let {
                        if (length % 2 == 0) {
                            return@let UwUfyBase.modifyLasts(it)
                        } else {
                            return@let UwUfyBase.modifyFirsts(it)
                        }
                    }
            } else pair.second
        }
        return if (newList.isNotEmpty() && newList.last().first.font == Style.DEFAULT_FONT) {
            val last = newList.last()
            buildList {
                addAll(newList.dropLast(1))
                add(last.first to "${last.second} ${PHRASES[length % PHRASES.size]}")
            }
        } else newList
    }

    override fun accept(visitor: FormattedCharSink): Boolean {
        var index = 0

        uwufiedStrings.forEach { (style, string) ->
            string.toCharArray().forEach { char ->
                index++
                if (!visitor.accept(index, style, char.code)) return false
            }
        }
        return true
    }

    public companion object {

        @JvmStatic
        public fun orderedToUwUText(sequence: FormattedCharSequence): UwUOrderedText {
            return UwUOrderedText(sequence)
        }
    }
}