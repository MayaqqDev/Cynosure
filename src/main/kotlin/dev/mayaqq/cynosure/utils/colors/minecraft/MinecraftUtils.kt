package dev.mayaqq.cynosure.utils.colors.minecraft

import dev.mayaqq.cynosure.utils.colors.Color
import dev.mayaqq.cynosure.utils.colors.Transparent
import dev.mayaqq.cynosure.utils.colors.withAlpha
import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.material.MapColor

public fun Color.toTextColor(): TextColor = TextColor.fromRgb(toInt() and 0x00ffffff)

public fun TextColor.toColor(): Color = Color(value) withAlpha 255

public val DyeColor.diffuseColor: Color
    get() {
        val colors = textureDiffuseColors
        return Color(colors[0], colors[1], colors[2])
    }

public val DyeColor.textColorAsColor: Color
    get() = Color(textColor)

public val DyeColor.fireworkColorAsColor: Color
    get() = Color(fireworkColor)

public val MapColor.asColor: Color
    get() = if (this == MapColor.NONE) Transparent else Color(col)