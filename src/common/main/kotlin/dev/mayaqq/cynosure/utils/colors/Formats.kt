package dev.mayaqq.cynosure.utils.colors

public fun Color(value: Int, format: ColorFormat): Color = format.decode(value)

public infix fun Color.with(format: ColorFormat): Int = format.encode(this)

public enum class ColorFormat {
    ARGB {
        override fun encode(c: Color): Int = c.argb.toInt()

        override fun decode(value: Int): Color = Color(value)
    },

    ABGR {
        override fun encode(c: Color): Int = c.alpha shl 24 or (c.blue shl 16) or (c.green shl 8) or c.red

        override fun decode(value: Int): Color =
            Color(value and 0xFF, (value shr 8) and 0xFF, (value shr 16) and 0xFF, value ushr 24)
    },

    RGBA {
        override fun encode(c: Color): Int = c.red shl 24 or (c.green shl 16) or (c.blue shl 8) or c.alpha

        override fun decode(value: Int): Color = Color(value ushr 24, (value shr 16) and 0xFF, (value shr 8) and 0xFF, value and 0xFF)

    },

    RGB {
        override fun encode(c: Color): Int = c.value and 0x00FFFFFF

        override fun decode(value: Int): Color = Color((value or (255 shl 24)).toUInt())
    },

    HSB {
        override fun encode(c: Color): Int {
            TODO("Not yet implemented")
        }

        override fun decode(value: Int): Color {
            TODO("Not yet implemented")
        }

    };

    internal abstract fun encode(c: Color): Int

    internal abstract fun decode(value: Int): Color
}