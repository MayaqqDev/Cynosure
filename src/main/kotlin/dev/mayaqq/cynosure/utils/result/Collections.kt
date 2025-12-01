package dev.mayaqq.cynosure.utils.result

public inline fun <A, B> Result<Iterable<A>>.mapElements(transform: (A) -> B): Result<List<B>> = map { it.map(transform) }

//public fun <A, B> Result<Sequence<A>>.mapElements(transform: (A) -> B): Result<Sequence<B>> = map { it.map(transform) }

public fun <A> Iterable<Result<A>>.gather(): Result<List<A>> = gatherTo(::mutableListOf)

public inline fun <A, C : MutableCollection<in A>> Iterable<Result<A>>.gatherTo(to: () -> C): Result<C> {
    return mapTo(to()) {
        if(it.isFailure) return it.exceptionOrNull()!!.failure()
        else it.getOrThrow()
    }.success()
}

public fun <A> Sequence<Result<A>>.gather(): Result<List<A>> = gatherTo(::mutableListOf)

public inline fun <A, C : MutableCollection<in A>> Sequence<Result<A>>.gatherTo(to: () -> C): Result<C> {
    val result = to()
    forEach {
        if(it.isFailure) return it.exceptionOrNull()!!.failure()
        else result.add(it.getOrThrow())
    }
    return result.success()
}

public inline fun <A, C : MutableCollection<in Result<A>>> Result<Iterable<A>>.splitTo(to:  C): C =
    fold({ it.mapTo(to) { i -> i.success() } }, { t -> to.also { it.add(t.failure()) } })

public fun <A> Result<Iterable<A>>.split(): List<Result<A>> = splitTo(mutableListOf())

public fun <A> Result<Sequence<A>>.split(): Sequence<Result<A>> =
    fold({ it.map { i -> i.success() } }, { sequenceOf(it.failure()) })