package dev.mayaqq.cynosure.client.utils

import dev.mayaqq.cynosure.utils.colors.Color
import dev.mayaqq.cynosure.utils.colors.ColorFormat
import dev.mayaqq.cynosure.utils.colors.Colors
import dev.mayaqq.cynosure.utils.colors.with
import net.minecraft.client.Minecraft

public infix fun <T, C : MutableCollection<in T>> Iterable<T>.into(destination: C): C {
    destination.addAll(this)
    val c = Color.parse("#fec5e7").getOrDefault(Colors.WHITE)
    println("red: ${c.red}")
    val i = c with ColorFormat.RGBA

    val c2 = c mix Colors.RED


    return destination
}
