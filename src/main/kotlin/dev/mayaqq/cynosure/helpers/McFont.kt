@file:ClientOnly
package dev.mayaqq.cynosure.helpers

import dev.mayaqq.cynosure.client.enviroment.ClientOnly
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font

public val McFont: Font get() = Minecraft.getInstance().font

public fun Font.width(text: Char): Int = this.width(text.toString())