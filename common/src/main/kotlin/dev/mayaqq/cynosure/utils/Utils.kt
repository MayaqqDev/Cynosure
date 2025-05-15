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

@JvmName("constant")
public fun <V> constant(constantValue: V): ConstantProperty<V> = ConstantProperty(constantValue)

public fun <I : Any, V> mapBacked(default: V): MapBackedProperty<I, V> = MapBackedProperty.Defaulted(default)

public fun <I : Any, V> mapBacked(initializer: (I) -> V): MapBackedProperty<I, V> = MapBackedProperty.Intialized(initializer)

public operator fun <T> ThreadLocal<T>.getValue(thisRef: Any?, property: KProperty<*>): T = get()

public operator fun <T> ThreadLocal<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    set(value)
}

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


public sealed class MapBackedProperty<I : Any, V> : ReadWriteProperty<I, V> {

    protected abstract fun getInitial(thisRef: I): V

    protected val map: MutableMap<I, V> = MapMaker().weakKeys().makeMap()

    override fun getValue(thisRef: I, property: KProperty<*>): V = map[thisRef] ?: getInitial(thisRef)

    override fun setValue(thisRef: I, property: KProperty<*>, value: V) {
        map[thisRef] = value
    }

    internal class Intialized<I : Any, V>(val initializer: (I) -> V) : MapBackedProperty<I, V>() {
        override fun getInitial(thisRef: I): V = initializer(thisRef).also { map[thisRef] = it }
    }

    internal class Defaulted<I : Any, V>(val default: V) : MapBackedProperty<I, V>() {
        override fun getInitial(thisRef: I): V = default
    }
}


@JvmInline
public value class ConstantProperty<V> internal constructor(private val value: V) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V = value
}

internal inline fun <reified S> loadService(loader: ClassLoader = S::class.java.classLoader): ServiceLoader<S> = ServiceLoader.load(S::class.java, S::class.java.classLoader)

public operator fun <R, C, V> Table<R, C, V>.get(row: R, column: C): V? = get(row, column)

public operator fun <R, C, V> Table<R, C, V>.set(row: R, column: C, value: V) {
    put(row, column, value)
}

public fun <A, B> Pair<A, B>.swap(): Pair<B, A> = second to first