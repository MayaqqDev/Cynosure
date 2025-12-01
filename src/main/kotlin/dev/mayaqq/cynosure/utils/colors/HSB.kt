package dev.mayaqq.cynosure.utils.colors

import dev.mayaqq.cynosure.utils.atLeast
import dev.mayaqq.cynosure.utils.atMost
import net.minecraft.util.Mth

public fun Color.Companion.fromHSB(hue: Float, saturation: Float, brightness: Float): Color = Color(Mth.hsvToRgb(hue, saturation, brightness))

public val Color.hue: Float get() {
    val min = (red atMost green atMost blue) / 255f
    val max = (red atLeast green atLeast blue) / 255f

    val r = floatRed; val g = floatGreen; val b = floatBlue
    return (when (max) {
        min -> 0f
        r -> (g - b) / (max - min)
        g -> 2f + (b - r) / (max - min)
        b -> 4f + (r - g) / (max - min)
        else -> 0f
    } * 60f).let { if(it < 0) it + 360f else it }
}

public val Color.saturation: Float get() {
    val min = red.coerceAtMost(green).coerceAtMost(blue) / 255f
    val max = red.coerceAtLeast(green).coerceAtLeast(blue) / 255f


    TODO()
}

public val Color.brightness: Float get() = TODO()