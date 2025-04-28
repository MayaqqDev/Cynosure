package dev.mayaqq.cynosure.utils

import com.google.common.collect.MapMaker
import com.google.common.collect.Table
import com.mojang.blaze3d.pipeline.TextureTarget
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

public fun <V> constant(constantValue: V): ReadOnlyProperty<Any?, V> = ConstantProperty(constantValue)

public fun <I : Any, V> mapBacked(default: V): ReadWriteProperty<I, V> = MapBackedProperty.Defaulted(default)

public fun <I : Any, V> mapBacked(initializer: (I) -> V): ReadWriteProperty<I, V> = MapBackedProperty.Intialized(initializer)

public operator fun <T> ThreadLocal<T>.getValue(thisRef: Any?, property: KProperty<*>): T = get()

public infix fun <T, C : MutableCollection<in T>> Iterable<T>.into(destination: C): C {
    destination.addAll(this)
    return destination
}

internal fun String.camelToSnakeCase(): String {
    val builder = StringBuilder(this.lowercase())

    forEachIndexed { index, ch ->
        if (ch.isUpperCase()) {
            if (this.getOrNull(index - 1)?.takeIf(Char::isLetter)?.isUpperCase() == false) {
                builder.insert(index, '_')
            }
        }
    }
    return builder.toString();
}


internal sealed class MapBackedProperty<I : Any, V> : ReadWriteProperty<I, V> {

    abstract fun getInitial(thisRef: I): V

    private val map: MutableMap<I, V> = MapMaker().weakKeys().makeMap()

    override fun getValue(thisRef: I, property: KProperty<*>): V = map[thisRef] ?: getInitial(thisRef)

    override fun setValue(thisRef: I, property: KProperty<*>, value: V) {
        map[thisRef] = value
    }

    internal class Intialized<I : Any, V>(val initializer: (I) -> V) : MapBackedProperty<I, V>() {
        override fun getInitial(thisRef: I): V = initializer(thisRef)
    }

    internal class Defaulted<I : Any, V>(val default: V) : MapBackedProperty<I, V>() {
        override fun getInitial(thisRef: I): V = default
    }
}


internal class ConstantProperty<V>(private val value: V) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V = value
}

internal inline fun <reified S> loadService(loader: ClassLoader = S::class.java.classLoader): ServiceLoader<S> = ServiceLoader.load(S::class.java, S::class.java.classLoader)

public operator fun <R, C, V> Table<R, C, V>.get(row: R, column: C): V? = get(row, column)

public operator fun <R, C, V> Table<R, C, V>.set(row: R, column: C, value: V) {
    put(row, column, value)
}