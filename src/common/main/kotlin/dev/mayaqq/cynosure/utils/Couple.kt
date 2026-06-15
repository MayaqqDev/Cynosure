package dev.mayaqq.cynosure.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public typealias Couple<T> = Pair<T, T>

public operator fun <T> Couple<T>.get(second: Boolean) = if (second) this.second else this.first

public operator fun <T> Couple<T>.get(index: Int) = get(index > 0)

public operator fun <T> Couple<T>.iterator(): Iterator<T> = Coupleater(this)

public operator fun <T> Couple<T>.contains(item: T): Boolean =
    first == item || second == item

@JvmName("mapCouple")
public inline fun <T, S> Couple<T>.map(mapper: (T) -> S): Couple<S> = mapper(first) to mapper(second)

public inline fun <T, S> Couple<T>.mapIndexed(mapper: (index: Int, T) -> S): Couple<S> =
    mapper(0, first) to mapper(1, second)

public inline fun <T> Couple<T>.both(predicate: (T) -> Boolean): Boolean =
    predicate(first) && predicate(second)

public inline fun <T> Couple<T>.either(predicate: (T) -> Boolean): Boolean =
    predicate(first) || predicate(second)

@OptIn(ExperimentalContracts::class)
public inline fun <T> Couple<T>.forEach(action: (T) -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.AT_LEAST_ONCE)
    }
    action(first)
    action(second)
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> Couple<T>.forEachIndexed(action: (index: Int, T) -> Unit) {
    contract {
        callsInPlace(action, InvocationKind.AT_LEAST_ONCE)
    }
    action(0, first)
    action(1, second)
}

public fun <T> Couple<T>.toList(): List<T> = listOf(first, second)

public fun <T, C : MutableCollection<T>> Couple<T>.toCollection(collection: C): C {
    collection.add(first)
    collection.add(second)
    return collection
}

public fun <T, C : MutableCollection<T>> Collection<Couple<T>>.flattenTo(collection: C): C {
    forEach { it.forEach(collection::add) }
    return collection
}

public fun <T> Collection<Couple<T>>.flatten(): List<T> = flatMap { it.toList() }

internal class Coupleater<T>(
    private val couple: Couple<T>
) : Iterator<T> {

    var state: Int = 0

    override fun next(): T {
        state++
        return when (state) {
            1 -> couple.first
            2 -> couple.second
            else -> throw IndexOutOfBoundsException("Iterator out of bounds. Couple only has 2 elements")
        }
    }

    override fun hasNext(): Boolean = state < 2
}