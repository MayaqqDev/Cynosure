package dev.mayaqq.cynosure.utils.colors

// Some extensions just to keep the class itself cleaner
public inline val Color.floatRed: Float get() = red / 255f

public inline val Color.floatGreen: Float get() = green / 255f

public inline val Color.floatBlue: Float get() = blue / 255f

public inline val Color.floatAlpha: Float get() = alpha / 255f

public fun Color.lighter(): Color = mix(White, 0.25f)

public fun Color.darker(): Color = mix(Black, 0.25f)

public infix fun Color.withRed(red: Int): Color = Color((value and 0xff00ffffu.toInt()) or ((red and 0xff) shl 16))

public infix fun Color.withGreen(green: Int): Color = Color((value and 0xffff00ffu.toInt()) or ((green and 0xff) shl 8))

public infix fun Color.withBlue(blue: Int): Color = Color((value and 0xffffff00u.toInt()) or (blue and 0xff))

public infix fun Color.withAlpha(alpha: Int): Color = Color((value and 0x00ffffff) or ((alpha and 0xff) shl 24))

public fun Color.toUInt(): UInt = toInt().toUInt()

public fun Color.toUInt(format: ColorFormat): UInt = toInt(format).toUInt()

@OptIn(ExperimentalStdlibApi::class)
public fun Color.toHex(): String = toUInt().toHexString(HexFormat.UpperCase)