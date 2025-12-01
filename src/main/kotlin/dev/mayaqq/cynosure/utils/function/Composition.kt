package dev.mayaqq.cynosure.utils.function

@JvmName("compose")
public operator fun <T, V, R> ((T) -> V).plus(other: (V) -> R): (T) -> R = { other(this(it)) }

@JvmName("andThen")
public operator fun <T> ((T) -> Unit).plus(other: (T) -> Unit): (T) -> Unit = { this(it); other(it) }

public infix fun <T> ((T) -> Boolean).and(other: (T) -> Boolean): (T) -> Boolean = { this(it) && other(it) }

public infix fun <T> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean = { this(it) || other(it) }

public infix fun <T> ((T) -> Boolean).xor(other: (T) -> Boolean): (T) -> Boolean = { this(it) xor other(it) }