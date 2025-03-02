package dev.mayaqq.cynosure.utils.codecs

import com.google.common.base.Suppliers
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import java.util.function.Supplier
import java.util.function.UnaryOperator


public fun <T> alternatives(vararg codecs: Codec<T>): Codec<T> = AlternativesCodec(codecs.toList())

public fun <T> Result<T>.toDataResult(): DataResult<T> = when {
    isSuccess -> DataResult.success(getOrThrow())
    else -> DataResult.error { exceptionOrNull()!!.message }
}

public class AlternativesCodec<A>(
    private val codecs: List<Codec<A>>
) : Codec<A> {

    override fun <T : Any?> encode(input: A, ops: DynamicOps<T>, prefix: T): DataResult<T> = codecs[0].encode(input, ops, prefix)


    override fun <T : Any?> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<A, T>> =
        codecs.map { codec -> codec.decode(ops, input) }.find { it.result().isPresent } ?: DataResult.error {""}
}

public class RecursiveCodec<T> internal constructor(private val name: String, wrapped: UnaryOperator<Codec<T>>) : Codec<T> {
    private val wrapped: Supplier<Codec<T>> = Suppliers.memoize {
        wrapped.apply(this)
    }

    override fun <S> decode(ops: DynamicOps<S>, input: S): DataResult<Pair<T, S>> {
        return wrapped.get().decode(ops, input)
    }

    override fun <S> encode(input: T, ops: DynamicOps<S>, prefix: S): DataResult<S> {
        return wrapped.get().encode(input, ops, prefix)
    }

    override fun toString(): String {
        return "RecursiveCodec[$name]"
    }
}