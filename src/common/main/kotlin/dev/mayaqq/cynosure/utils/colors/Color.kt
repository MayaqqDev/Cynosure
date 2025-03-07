package dev.mayaqq.cynosure.utils.colors

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.mayaqq.cynosure.utils.codecs.alternatives
import dev.mayaqq.cynosure.utils.codecs.toDataResult


@JvmInline
public value class Color internal constructor(internal val value: Int) {

    public companion object {
        public val CODEC: Codec<Color> = alternatives(
            Codec.INT.xmap(::Color, Color::value),
            Codec.STRING.comapFlatMap({ tryParse(it).toDataResult() }, Color::toHex),
            RecordCodecBuilder.create { it.group(
                Codec.INT.fieldOf("red").forGetter(Color::red),
                Codec.INT.fieldOf("green").forGetter(Color::green),
                Codec.INT.fieldOf("blue").forGetter(Color::blue),
                Codec.INT.optionalFieldOf("alpha", 255).forGetter(Color::alpha)
            ).apply(it, ::Color) }
        )

        public fun parse(data: String, format: ColorFormat = ColorFormat.ARGB): Color = tryParse(data, format).getOrThrow()

        @OptIn(ExperimentalStdlibApi::class)
        public fun tryParse(from: String, format: ColorFormat = ColorFormat.ARGB): Result<Color> = when {
            namedColors.contains(from) -> Result.success(namedColors[from]!!)
            from.startsWith('#') -> from.substring(1).runCatching(String::hexToInt).map { Color(it, format) }
            from.startsWith("0x") -> from.substring(2).runCatching(String::hexToInt).map { Color(it, format) }
            else -> Result.failure(IllegalArgumentException("Invalid color format"))
        }
    }

    public constructor(argb: UInt) : this(argb.toInt())

    public constructor(red: Int, green: Int, blue: Int, alpha: Int = 255)
            : this((alpha shl 24) or (red shl 16) or (green shl 8) or blue)

    public constructor(red: Float, green: Float, blue: Float, alpha: Float = 1.0f)
            : this((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), (alpha * 255).toInt())

    public val red: Int get() = (value shr 16) and 0xFF

    public val green: Int get() = (value shr 8) and 0xFF

    public val blue: Int get() = value and 0xFF

    public val alpha: Int get() = value ushr 24

    public val argb: UInt get() = value.toUInt()

    public infix fun mix(other: Color): Color = this.mix(other, 0.5f)

    public fun mix(other: Color, weight: Float): Color = Color(
        (this.red + (other.red - this.red) * weight).toInt(),
        (this.green + (other.green - this.green) * weight).toInt(),
        (this.blue + (other.blue - this.blue) * weight).toInt(),
        (this.alpha + (other.alpha - this.alpha) * weight).toInt()
    )

    public operator fun component1(): Int = red

    public operator fun component2(): Int = green

    public operator fun component3(): Int = blue

    public operator fun component4(): Int = alpha

    override fun toString(): String {
        return "Color[$red, $green, $blue, $alpha]"
    }
}

// Some extensions just to keep the class itself cleaner
public val Color.floatRed: Float get() = red / 255f

public val Color.floatGreen: Float get() = green / 255f

public val Color.floatBlue: Float get() = blue / 255f

public val Color.floatAlpha: Float get() = alpha / 255f

public fun Color.lighter(): Color = mix(Colors.WHITE, 0.25f)

public fun Color.darker(): Color = mix(Colors.BLACK, 0.25f)

@OptIn(ExperimentalStdlibApi::class)
public fun Color.toHex(): String = argb.toHexString(HexFormat.UpperCase)

