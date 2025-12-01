package dev.mayaqq.cynosure.utils.colors

import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

public enum class ColorFormat {
    ARGB {
        override fun toInt(c: Color): Int = c.value

        override fun toColor(value: Int): Color = Color(value)

        override fun parseString(data: String): Color = data.split(",")
            .mapTo(IntArrayList(), String::toInt)
            .let { Color(it.getInt(1), it.getInt(2), it.getInt(3), it.getInt(0)) }
    },

    ABGR {
        override fun toInt(c: Color): Int = c.alpha shl 24 or (c.blue shl 16) or (c.green shl 8) or c.red

        override fun toColor(value: Int): Color =
            Color(value and 0xFF, (value shr 8) and 0xFF, (value shr 16) and 0xFF, value ushr 24)

        override fun parseString(data: String): Color = parseToIntList(data)
            .let { Color(it.getInt(3), it.getInt(2), it.getInt(1), it.getInt(0)) }
    },

    RGBA {
        override fun toInt(c: Color): Int = (c.red shl 24) or (c.green shl 16) or (c.blue shl 8) or c.alpha

        override fun toColor(value: Int): Color = Color(value ushr 24, (value shr 16) and 0xFF, (value shr 8) and 0xFF, value and 0xFF)

        override fun parseString(data: String): Color = parseToIntList(data)
            .let { Color(it.getInt(0), it.getInt(1), it.getInt(2), it.getInt(3)) }

    },

    RGB {
        override fun toInt(c: Color): Int = c.value and 0x00FFFFFF

        override fun toColor(value: Int): Color = Color(value or (255 shl 24))

        override fun parseString(data: String): Color = parseToIntList(data)
            .let { Color(it.getInt(0), it.getInt(1), it.getInt(2)) }
    },

    HSB {
        override fun toInt(c: Color): Int {
            TODO("Not yet implemented")
        }

        override fun toColor(value: Int): Color {
            TODO("Not yet implemented")
        }

        override fun parseString(data: String): Color {
            TODO("Not yet implemented")
        }

    };

    protected fun parseToIntList(string: String): IntList = string.split(",")
        .mapTo(IntArrayList()) { it.trim().toInt() }

    internal abstract fun toInt(c: Color): Int

    internal abstract fun toColor(value: Int): Color

    internal abstract fun parseString(data: String): Color
}