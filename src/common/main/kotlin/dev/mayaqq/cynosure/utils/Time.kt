package dev.mayaqq.cynosure.utils

import net.minecraft.world.level.Level
import kotlin.math.pow

/**
 * Store the world time as a double precision floating point number.
 *
 * @param world The world to get the time of.
 * @return Current time. Number of ticks.
 */
public fun currentTime(world: Level): Double {
    val ticks = world.gameTime
    if (ticks >= 0 && ticks <= 2.0.pow(53.0)) {
        return ticks.toDouble()
    } else {
        throw ArithmeticException("overflow")
    }
}