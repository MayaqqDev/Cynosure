@file:Suppress("NOTHING_TO_INLINE")
package dev.mayaqq.cynosure.utils.vectors

import org.joml.Vector2fc


public inline operator fun Vector2fc.component1(): Float = x()

public inline operator fun Vector2fc.component2(): Float = y()

