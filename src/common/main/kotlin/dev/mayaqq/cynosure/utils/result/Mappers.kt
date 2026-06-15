package dev.mayaqq.cynosure.utils.result


// flatMap
public inline fun <A, B> Result<A>.flatMap(transform: (A) -> Result<B>): Result<B> =
    fold({ transform(it) }, { it.failure() })

public inline fun <A, B> Result<A?>.flatMapIfNotNull(transform: (A) -> Result<B>): Result<B?> =
    flatMap { if(it === null) null.success() else transform(it) }

public inline fun <A> Result<A?>.flatMapIfNull(default: () -> Result<A>): Result<A> =
    flatMap { it?.success() ?: default() }

public inline fun <A, B, reified X : Throwable> Result<A>.mapCatchingSpecific(transform: (A) -> B): Result<B> =
    flatMap { it.runCatchingSpecific<_, _, X>(transform) }

public inline fun <A, B> Result<A?>.mapIfNotNull(transform: (A) -> B): Result<B?> =
    map { if(it == null) null else transform(it) }

public inline fun <A, B> Result<A?>.mapCatchingIfNotNull(tranform: (A) -> B): Result<B?> =
    mapCatching { if(it == null) null else tranform(it) }

public inline fun <A, B, reified X : Throwable> Result<A?>.mapCatchingSpecificIfNotNull(transform: (A) -> B): Result<B?> =
    mapCatchingSpecific<_, _, X> { if(it == null) null else transform(it) }

public inline fun <A> Result<A?>.mapIfNull(transform: () -> A): Result<A> =
    map { it ?: transform() }

public inline fun <A> Result<A?>.mapCatchingIfNull(transform: () -> A): Result<A> =
    mapCatching { it ?: transform() }

public inline fun <A, reified X : Throwable> Result<A?>.mapCatchingSpecificIfNull(transform: () -> A): Result<A> =
    mapCatchingSpecific<_, _, X> { it ?: transform() }

public inline fun <A> Result<A>.mapError(transform: (Throwable) -> Throwable): Result<A> =
    fold({ it.success() }, { transform(it).failure() })


public inline fun <A> Result<A>.flatRecover(func: (Throwable) -> Result<A>): Result<A> =
    if(this.isFailure) func(exceptionOrNull()!!) else this