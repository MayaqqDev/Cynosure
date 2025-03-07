package dev.mayaqq.cynosure.utils.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.ObjectEntryByteCodec
import kotlin.reflect.KProperty1


public fun <T> alternatives(vararg codecs: Codec<T>): Codec<T> = AlternativesCodec(codecs.toList())

public infix fun <O, A> Codec<A>.fieldOf(field: KProperty1<O, A>): RecordCodecBuilder<O, A> =
    fieldOf(field.name).forGetter(field)

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

public fun <T> recursive(name: String = "", wrapped: Codec<T>.() -> Codec<T>): RecursiveCodec<T> = RecursiveCodec(name, wrapped)

public class RecursiveCodec<T> internal constructor(private val name: String, wrapped: (Codec<T>) -> Codec<T>) : Codec<T> {

    private val wrapped: Codec<T> by lazy { wrapped(this) }

    override fun <S> decode(ops: DynamicOps<S>, input: S): DataResult<Pair<T, S>> {
        return wrapped.decode(ops, input)
    }

    override fun <S> encode(input: T, ops: DynamicOps<S>, prefix: S): DataResult<S> {
        return wrapped.encode(input, ops, prefix)
    }

    override fun toString(): String {
        return "RecursiveCodec[$name]"
    }
}

public infix fun <O, T> ByteCodec<T>.fieldOf(getter: (O) -> T): ObjectEntryByteCodec<O, T> =
    ObjectEntryByteCodec(this, getter)