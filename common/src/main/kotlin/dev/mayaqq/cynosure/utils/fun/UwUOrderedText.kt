package dev.mayaqq.cynosure.utils.`fun`

import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.FormattedCharSink

public class UwUOrderedText(private val wrapped: FormattedCharSequence, private val uwufiedString: String = fchsToString(wrapped).uwufy()) : FormattedCharSequence {
    override fun accept(visitor: FormattedCharSink): Boolean {
        return wrapped.accept { index, style, codePoint ->
            uwufiedString.toCharArray().forEach { char ->
                if (!visitor.accept(index, style, char.code)) return@accept false
            }
            return@accept false
        }
    }

    public companion object {
        public fun fchsToString(sequence: FormattedCharSequence): String {
            var builder = StringBuilder()
            sequence.accept { index, style, codePoint ->
                builder.appendCodePoint(codePoint)
                return@accept true
            }
            return builder.toString()
        }

        @JvmStatic
        public fun orderedToUwUText(sequence: FormattedCharSequence): UwUOrderedText {
            return UwUOrderedText(sequence)
        }
    }
}