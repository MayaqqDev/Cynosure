package dev.mayaqq.cynosure.client.utils.client

import org.joml.Vector2fc
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector4fc

public fun Vector3f.grow(nx: Float, ny: Float, nz: Float): Vector3f =
    set(nx.coerceAtLeast(this.x), ny.coerceAtLeast(this.y), nz.coerceAtLeast(this.z))

public fun Vector3f.shrink(nx: Float, ny: Float, nz: Float): Vector3f =
    set(nx.coerceAtMost(this.x), ny.coerceAtMost(this.y), nz.coerceAtMost(this.z))

public inline operator fun Vector4fc.component0() = x()

public inline operator fun Vector4fc.component1() = y()

public inline operator fun Vector4fc.component2() = z()

public inline operator fun Vector4fc.component3() = w()

public inline operator fun Vector3fc.component0() = x()

public inline operator fun Vector3fc.component1() = y()

public inline operator fun Vector3fc.component2() = z()

public inline operator fun Vector2fc.component0() = x()

public inline operator fun Vector2fc.component1() = x()

