package dev.mayaqq.cynosure.core.codecs

import com.google.gson.JsonElement
import com.mojang.serialization.*
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.ObjectEntryByteCodec
import dev.mayaqq.cynosure.utils.Either
import dev.mayaqq.cynosure.utils.dfu.DFUPair
import dev.mayaqq.cynosure.utils.dfu.toDFU
import dev.mayaqq.cynosure.utils.dfu.toKt
import dev.mayaqq.cynosure.utils.foldToLeft
import dev.mayaqq.cynosure.utils.result.toDataResult
import net.minecraft.core.Registry
import kotlin.reflect.KProperty1


public object Codecs {
    @JvmStatic
    public fun <T> alternatives(vararg codecs: Codec<T>): Codec<T> = AlternativesCodec(codecs.toList())

    @JvmStatic
    public fun <T> recursive(name: String = "", wrapped: Codec<T>.() -> Codec<T>): RecursiveCodec<T> = RecursiveCodec(name, wrapped)

    @JvmStatic
    public fun <T> lazy(initializer: () -> Codec<T>): LazyCodec<T> = LazyCodec(initializer)

    @JvmStatic
    public fun <L, R> either(leftCodec: Codec<L>, rightCodec: Codec<R>): Codec<Either<L, R>> =
        Either.codec(leftCodec, rightCodec)

    @JvmStatic
    public fun <A, B> pair(leftCodec: Codec<A>, righCodec: Codec<B>): Codec<Pair<A, B>> =
        Codec.pair(leftCodec, righCodec).xmap(DFUPair<A, B>::toKt, Pair<A, B>::toDFU)

    @JvmStatic
    public fun <A> json(encoder: (A) -> JsonElement, decoder: (JsonElement) -> A): Codec<A> =
        Codec.PASSTHROUGH.flatXmap(
            fun(dynamic) = dynamic.convert(JsonOps.INSTANCE).value.runCatching(decoder).toDataResult(),
            fun(a) = a.runCatching(encoder).map { Dynamic(JsonOps.INSTANCE, it) }.toDataResult()
        )

    @JvmStatic
    public fun <T> registryId(registry: Registry<T>) : Codec<T> {
        return Codec.INT.comapFlatMap({ value ->
            val t = registry.byId(value)
            if (t == null) {
                DataResult.error<T>{ "Unknown registry value: $value" }
            }
            DataResult.success(t)
        }, registry::getId)
    }
}

public fun <T> Registry<T>.byIdCodec(): Codec<T> = Codecs.registryId(this)

public infix fun <O, A> Codec<A>.fieldOf(field: KProperty1<O, A>): RecordCodecBuilder<O, A> =
    fieldOf(field.name).forGetter(field)

public infix fun <A> Codec<A>.fieldOf(name: String): MapCodec<A> = fieldOf(name)

public infix fun <O, A> MapCodec<A>.forGetter(getter: (O) -> A): RecordCodecBuilder<O, A> = forGetter(getter)

public infix fun <O, T> ByteCodec<T>.fieldOf(getter: (O) -> T): ObjectEntryByteCodec<O, T> =
    ObjectEntryByteCodec(this, getter)