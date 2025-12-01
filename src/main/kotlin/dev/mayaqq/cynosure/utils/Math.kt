package dev.mayaqq.cynosure.utils

public const val PI: Double = java.lang.Math.PI
public const val DEG_TO_RAD: Double = PI / 180.0
public const val RAD_TO_DEG: Double = 180.0 / PI

public fun Float.normalized() = coerceIn(-1f, 1f)

public inline val Float.radians: Float get() = this * DEG_TO_RAD.toFloat()

public inline val Float.degrees: Float get() = this * RAD_TO_DEG.toFloat()

public inline val Double.radians: Double get() = this * DEG_TO_RAD

public inline val Double.degrees: Double get() = this * RAD_TO_DEG

public infix fun Int.atMost(max: Int): Int = coerceAtMost(max)

public infix fun Long.atMost(max: Long): Long = coerceAtMost(max)

public infix fun Float.atMost(max: Float): Float = coerceAtMost(max)

public infix fun Double.atMost(max: Double): Double = coerceAtMost(max)

public infix fun Int.atLeast(min: Int): Int = coerceAtLeast(min)

public infix fun Long.atLeast(min: Long): Long = coerceAtLeast(min)

public infix fun Float.atLeast(min: Float): Float = coerceAtLeast(min)

public infix fun Double.atLeast(min: Double): Double = coerceAtLeast(min)