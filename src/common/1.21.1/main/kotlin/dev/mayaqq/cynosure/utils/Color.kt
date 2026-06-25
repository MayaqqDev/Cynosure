package dev.mayaqq.cynosure.utils

import invoke.kitty.kritter.utils.color.Color
import invoke.kitty.kritter.utils.color.rgb
import net.minecraft.world.item.DyeColor

public actual val DyeColor.diffuseColor: Color
    get() = textureDiffuseColor.rgb