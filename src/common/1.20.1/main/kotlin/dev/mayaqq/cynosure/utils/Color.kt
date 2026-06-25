package dev.mayaqq.cynosure.utils

import invoke.kitty.kritter.utils.color.Color
import invoke.kitty.kritter.utils.color.rgb
import net.minecraft.world.item.DyeColor

public actual val DyeColor.diffuseColor: Color
    get() {
        val colors = textureDiffuseColors
        return rgb(colors[0], colors[1], colors[2])
    }