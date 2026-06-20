package dev.mayaqq.cynosure.core.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps

internal class EnumCodec<T>(private val codec: Codec<T>) : Codec<T> {

    override fun <T1 : Any?> encode(input: T, ops: DynamicOps<T1>?, prefix: T1): DataResult<T1> = codec.encode(input, ops, prefix)
    override fun <T1 : Any?> decode(ops: DynamicOps<T1>?, input: T1): DataResult<com.mojang.datafixers.util.Pair<T, T1>> = codec.decode(ops, input)

    companion object {

        inline fun <reified T: Enum<T>> of(): EnumCodec<T> =
            EnumCodec(Codecs.alternatives(constantCodec(T::class.java.enumConstants), intCodec(T::class.java.enumConstants)))

        internal fun <T> intCodec(constants: Array<T>): Codec<T> {
            return Codec.INT.flatXmap(
                { ordinal: Int ->
                    if (ordinal >= 0 && ordinal < constants.size) {
                        return@flatXmap DataResult.success<T>(constants[ordinal])
                    }
                    DataResult.error { "Unknown enum ordinal: ${'$'}ordinal" }
                },
                { value: T -> DataResult.success((value as Enum<*>).ordinal) },
            )
        }

        internal fun <T> constantCodec(constants: Array<T>): Codec<T> = Codec.STRING.flatXmap(
            { name: String ->
                runCatching {
                    DataResult.success(constants.first { (it as Enum<*>).name.equals(name, true) })
                }.getOrElse {
                    DataResult.error { "Unknown enum name: ${'$'}name" }
                }
            },
            { value: T -> DataResult.success((value as Enum<*>).name) },
        )
    }
}