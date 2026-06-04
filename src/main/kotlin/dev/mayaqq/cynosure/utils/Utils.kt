package dev.mayaqq.cynosure.utils

import com.google.common.collect.MapMaker
import com.google.common.collect.Table
import dev.mayaqq.cynosure.core.Environment
import net.minecraft.world.level.Level
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


@OptIn(ExperimentalContracts::class)
public inline fun <T> make(thing: T, maker: T.() -> Unit): T {
    contract {
        callsInPlace(maker, InvocationKind.EXACTLY_ONCE)
    }
    maker(thing)
    return thing
}

public val Level.side: Environment
    get() = if (isClientSide) Environment.CLIENT else Environment.SERVER

internal inline fun <reified S> loadService(loader: ClassLoader = S::class.java.classLoader): ServiceLoader<S> = ServiceLoader.load(S::class.java, S::class.java.classLoader)

public operator fun <R, C, V> Table<R, C, V>.get(row: R, column: C): V? = get(row, column)

public operator fun <R, C, V> Table<R, C, V>.set(row: R, column: C, value: V) {
    put(row, column, value)
}

public fun <A, B> Pair<A, B>.swap(): Pair<B, A> = second to first