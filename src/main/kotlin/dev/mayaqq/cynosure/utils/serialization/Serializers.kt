@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("UNCHECKED_CAST")
package dev.mayaqq.cynosure.utils.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

public inline fun <T, reified R> KSerializer<T>.map(crossinline to: (T) -> R, crossinline  from: (R) -> T): KSerializer<R> =
    map(R::class.qualifiedName!!, to, from)

public inline fun <T, R> KSerializer<T>.map(name: String, crossinline to: (T) -> R, crossinline  from: (R) -> T): KSerializer<R> = object : KSerializer<R> {
    override fun deserialize(decoder: Decoder): R = to(this@map.deserialize(decoder))

    override val descriptor: SerialDescriptor = SerialDescriptor(name, this@map.descriptor)

    override fun serialize(encoder: Encoder, value: R) {
        this@map.serialize(encoder, from(value))
    }
}


public infix fun <S, T> KSerializer<T>.fieldOf(property: KProperty1<S, T>): KStructureComponent<S, T> {
    return KStructureComponent(property.findAnnotation<SerialName>()?.value ?: property.name, this, property)
}


public fun <S, T> KSerializer<T>.fieldOf(name: String, getter: (S) -> T): KStructureComponent<S, T> {
    return KStructureComponent(name, this, getter)
}

public inline operator fun <S, reified T> KProperty1<S, T>.unaryPlus(): KStructureComponent<S, T> = serializer<T>() fieldOf this

public fun <S> classSerializer(
    name: String,
    constructor: Function<S>,
    vararg components: KStructureComponent<S, *>
) {

    val ctor = constructor::class.memberFunctions.find { it.name == "invoke" }


}

// Function for arity 1
public inline fun <S, T1> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    crossinline constructor: (T1) -> S
): KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
        }
    }
}

// Function for arity 2
public inline fun <S, T1, T2> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    f2: KStructureComponent<S, T2>,
    crossinline constructor: (T1, T2) -> S
): KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
        element(f2.key, f2.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null
        var v2: T2? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        1 -> v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1, v2 as T2)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
            encodeSerializableElement(descriptor, 1, f2.serializer, f2.getter(value))
        }
    }
}


public inline fun <S, T1, T2, T3> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    f2: KStructureComponent<S, T2>,
    f3: KStructureComponent<S, T3>,
    crossinline constructor: (T1, T2, T3) -> S
) : KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
        element(f2.key, f2.serializer.descriptor)
        element(f3.key, f3.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null;
        var v2: T2? = null;
        var v3: T3? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        1 -> v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                        2 -> v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1, v2 as T2, v3 as T3)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
            encodeSerializableElement(descriptor, 1, f2.serializer, f2.getter(value))
            encodeSerializableElement(descriptor, 2, f3.serializer, f3.getter(value))
        }
    }
}

public inline fun <S, T1, T2, T3, T4> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    f2: KStructureComponent<S, T2>,
    f3: KStructureComponent<S, T3>,
    f4: KStructureComponent<S, T4>,
    crossinline constructor: (T1, T2, T3, T4) -> S
): KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
        element(f2.key, f2.serializer.descriptor)
        element(f3.key, f3.serializer.descriptor)
        element(f4.key, f4.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null
        var v2: T2? = null
        var v3: T3? = null
        var v4: T4? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        1 -> v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                        2 -> v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                        3 -> v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1, v2 as T2, v3 as T3, v4 as T4)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
            encodeSerializableElement(descriptor, 1, f2.serializer, f2.getter(value))
            encodeSerializableElement(descriptor, 2, f3.serializer, f3.getter(value))
            encodeSerializableElement(descriptor, 3, f4.serializer, f4.getter(value))
        }
    }
}

// The same structure is repeated for arities 5 through 16
// Below is the pattern for arity 5 as an example

public inline fun <S, T1, T2, T3, T4, T5> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    f2: KStructureComponent<S, T2>,
    f3: KStructureComponent<S, T3>,
    f4: KStructureComponent<S, T4>,
    f5: KStructureComponent<S, T5>,
    crossinline constructor: (T1, T2, T3, T4, T5) -> S
): KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
        element(f2.key, f2.serializer.descriptor)
        element(f3.key, f3.serializer.descriptor)
        element(f4.key, f4.serializer.descriptor)
        element(f5.key, f5.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null
        var v2: T2? = null
        var v3: T3? = null
        var v4: T4? = null
        var v5: T5? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        1 -> v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                        2 -> v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                        3 -> v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                        4 -> v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1, v2 as T2, v3 as T3, v4 as T4, v5 as T5)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
            encodeSerializableElement(descriptor, 1, f2.serializer, f2.getter(value))
            encodeSerializableElement(descriptor, 2, f3.serializer, f3.getter(value))
            encodeSerializableElement(descriptor, 3, f4.serializer, f4.getter(value))
            encodeSerializableElement(descriptor, 4, f5.serializer, f5.getter(value))
        }
    }
}

public inline fun <S, T1, T2, T3, T4, T5, T6> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    f2: KStructureComponent<S, T2>,
    f3: KStructureComponent<S, T3>,
    f4: KStructureComponent<S, T4>,
    f5: KStructureComponent<S, T5>,
    f6: KStructureComponent<S, T6>,
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> S
): KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
        element(f2.key, f2.serializer.descriptor)
        element(f3.key, f3.serializer.descriptor)
        element(f4.key, f4.serializer.descriptor)
        element(f5.key, f5.serializer.descriptor)
        element(f6.key, f6.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null
        var v2: T2? = null
        var v3: T3? = null
        var v4: T4? = null
        var v5: T5? = null
        var v6: T6? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        1 -> v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                        2 -> v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                        3 -> v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                        4 -> v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                        5 -> v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1, v2 as T2, v3 as T3, v4 as T4, v5 as T5, v6 as T6)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
            encodeSerializableElement(descriptor, 1, f2.serializer, f2.getter(value))
            encodeSerializableElement(descriptor, 2, f3.serializer, f3.getter(value))
            encodeSerializableElement(descriptor, 3, f4.serializer, f4.getter(value))
            encodeSerializableElement(descriptor, 4, f5.serializer, f5.getter(value))
            encodeSerializableElement(descriptor, 5, f6.serializer, f6.getter(value))
        }
    }
}

public inline fun <S, T1, T2, T3, T4, T5, T6, T7> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    f2: KStructureComponent<S, T2>,
    f3: KStructureComponent<S, T3>,
    f4: KStructureComponent<S, T4>,
    f5: KStructureComponent<S, T5>,
    f6: KStructureComponent<S, T6>,
    f7: KStructureComponent<S, T7>,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> S
) : KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
        element(f2.key, f2.serializer.descriptor)
        element(f3.key, f3.serializer.descriptor)
        element(f4.key, f4.serializer.descriptor)
        element(f5.key, f5.serializer.descriptor)
        element(f6.key, f6.serializer.descriptor)
        element(f7.key, f7.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null
        var v2: T2? = null
        var v3: T3? = null
        var v4: T4? = null
        var v5: T5? = null
        var v6: T6? = null
        var v7: T7? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
                v7 = decodeSerializableElement(descriptor, 6, f7.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        1 -> v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                        2 -> v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                        3 -> v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                        4 -> v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                        5 -> v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
                        6 -> v7 = decodeSerializableElement(descriptor, 6, f7.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1, v2 as T2, v3 as T3, v4 as T4, v5 as T5, v6 as T6, v7 as T7)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
            encodeSerializableElement(descriptor, 1, f2.serializer, f2.getter(value))
            encodeSerializableElement(descriptor, 2, f3.serializer, f3.getter(value))
            encodeSerializableElement(descriptor, 3, f4.serializer, f4.getter(value))
            encodeSerializableElement(descriptor, 4, f5.serializer, f5.getter(value))
            encodeSerializableElement(descriptor, 5, f6.serializer, f6.getter(value))
            encodeSerializableElement(descriptor, 6, f7.serializer, f7.getter(value))
        }
    }
}

public inline fun <S, T1, T2, T3, T4, T5, T6, T7, T8> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    f2: KStructureComponent<S, T2>,
    f3: KStructureComponent<S, T3>,
    f4: KStructureComponent<S, T4>,
    f5: KStructureComponent<S, T5>,
    f6: KStructureComponent<S, T6>,
    f7: KStructureComponent<S, T7>,
    f8: KStructureComponent<S, T8>,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> S
) : KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
        element(f2.key, f2.serializer.descriptor)
        element(f3.key, f3.serializer.descriptor)
        element(f4.key, f4.serializer.descriptor)
        element(f5.key, f5.serializer.descriptor)
        element(f6.key, f6.serializer.descriptor)
        element(f7.key, f7.serializer.descriptor)
        element(f8.key, f8.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null
        var v2: T2? = null
        var v3: T3? = null
        var v4: T4? = null
        var v5: T5? = null
        var v6: T6? = null
        var v7: T7? = null
        var v8: T8? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
                v7 = decodeSerializableElement(descriptor, 6, f7.serializer)
                v8 = decodeSerializableElement(descriptor, 7, f8.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        1 -> v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                        2 -> v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                        3 -> v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                        4 -> v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                        5 -> v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
                        6 -> v7 = decodeSerializableElement(descriptor, 6, f7.serializer)
                        7 -> v8 = decodeSerializableElement(descriptor, 7, f8.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1, v2 as T2, v3 as T3, v4 as T4, v5 as T5, v6 as T6, v7 as T7, v8 as T8)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
            encodeSerializableElement(descriptor, 1, f2.serializer, f2.getter(value))
            encodeSerializableElement(descriptor, 2, f3.serializer, f3.getter(value))
            encodeSerializableElement(descriptor, 3, f4.serializer, f4.getter(value))
            encodeSerializableElement(descriptor, 4, f5.serializer, f5.getter(value))
            encodeSerializableElement(descriptor, 5, f6.serializer, f6.getter(value))
            encodeSerializableElement(descriptor, 6, f7.serializer, f7.getter(value))
            encodeSerializableElement(descriptor, 7, f8.serializer, f8.getter(value))
        }
    }
}

// Arity 9
public inline fun <S, T1, T2, T3, T4, T5, T6, T7, T8, T9> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    f2: KStructureComponent<S, T2>,
    f3: KStructureComponent<S, T3>,
    f4: KStructureComponent<S, T4>,
    f5: KStructureComponent<S, T5>,
    f6: KStructureComponent<S, T6>,
    f7: KStructureComponent<S, T7>,
    f8: KStructureComponent<S, T8>,
    f9: KStructureComponent<S, T9>,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> S
) : KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
        element(f2.key, f2.serializer.descriptor)
        element(f3.key, f3.serializer.descriptor)
        element(f4.key, f4.serializer.descriptor)
        element(f5.key, f5.serializer.descriptor)
        element(f6.key, f6.serializer.descriptor)
        element(f7.key, f7.serializer.descriptor)
        element(f8.key, f8.serializer.descriptor)
        element(f9.key, f9.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null
        var v2: T2? = null
        var v3: T3? = null
        var v4: T4? = null
        var v5: T5? = null
        var v6: T6? = null
        var v7: T7? = null
        var v8: T8? = null
        var v9: T9? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
                v7 = decodeSerializableElement(descriptor, 6, f7.serializer)
                v8 = decodeSerializableElement(descriptor, 7, f8.serializer)
                v9 = decodeSerializableElement(descriptor, 8, f9.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        1 -> v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                        2 -> v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                        3 -> v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                        4 -> v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                        5 -> v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
                        6 -> v7 = decodeSerializableElement(descriptor, 6, f7.serializer)
                        7 -> v8 = decodeSerializableElement(descriptor, 7, f8.serializer)
                        8 -> v9 = decodeSerializableElement(descriptor, 8, f9.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1, v2 as T2, v3 as T3, v4 as T4, v5 as T5, v6 as T6, v7 as T7, v8 as T8, v9 as T9)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
            encodeSerializableElement(descriptor, 1, f2.serializer, f2.getter(value))
            encodeSerializableElement(descriptor, 2, f3.serializer, f3.getter(value))
            encodeSerializableElement(descriptor, 3, f4.serializer, f4.getter(value))
            encodeSerializableElement(descriptor, 4, f5.serializer, f5.getter(value))
            encodeSerializableElement(descriptor, 5, f6.serializer, f6.getter(value))
            encodeSerializableElement(descriptor, 6, f7.serializer, f7.getter(value))
            encodeSerializableElement(descriptor, 7, f8.serializer, f8.getter(value))
            encodeSerializableElement(descriptor, 8, f9.serializer, f9.getter(value))
        }
    }
}

public inline fun <S, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> buildClassSerializer(
    name: String,
    f1: KStructureComponent<S, T1>,
    f2: KStructureComponent<S, T2>,
    f3: KStructureComponent<S, T3>,
    f4: KStructureComponent<S, T4>,
    f5: KStructureComponent<S, T5>,
    f6: KStructureComponent<S, T6>,
    f7: KStructureComponent<S, T7>,
    f8: KStructureComponent<S, T8>,
    f9: KStructureComponent<S, T9>,
    f10: KStructureComponent<S, T10>,
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> S
) : KSerializer<S> = object : KSerializer<S> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(name) {
        element(f1.key, f1.serializer.descriptor)
        element(f2.key, f2.serializer.descriptor)
        element(f3.key, f3.serializer.descriptor)
        element(f4.key, f4.serializer.descriptor)
        element(f5.key, f5.serializer.descriptor)
        element(f6.key, f6.serializer.descriptor)
        element(f7.key, f7.serializer.descriptor)
        element(f8.key, f8.serializer.descriptor)
        element(f9.key, f9.serializer.descriptor)
        element(f10.key, f10.serializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): S {
        var v1: T1? = null
        var v2: T2? = null
        var v3: T3? = null
        var v4: T4? = null
        var v5: T5? = null
        var v6: T6? = null
        var v7: T7? = null
        var v8: T8? = null
        var v9: T9? = null
        var v10: T10? = null

        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
                v7 = decodeSerializableElement(descriptor, 6, f7.serializer)
                v8 = decodeSerializableElement(descriptor, 7, f8.serializer)
                v9 = decodeSerializableElement(descriptor, 8, f9.serializer)
                v10 = decodeSerializableElement(descriptor, 9, f10.serializer)
            } else {
                while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> v1 = decodeSerializableElement(descriptor, 0, f1.serializer)
                        1 -> v2 = decodeSerializableElement(descriptor, 1, f2.serializer)
                        2 -> v3 = decodeSerializableElement(descriptor, 2, f3.serializer)
                        3 -> v4 = decodeSerializableElement(descriptor, 3, f4.serializer)
                        4 -> v5 = decodeSerializableElement(descriptor, 4, f5.serializer)
                        5 -> v6 = decodeSerializableElement(descriptor, 5, f6.serializer)
                        6 -> v7 = decodeSerializableElement(descriptor, 6, f7.serializer)
                        7 -> v8 = decodeSerializableElement(descriptor, 7, f8.serializer)
                        8 -> v9 = decodeSerializableElement(descriptor, 8, f9.serializer)
                        9 -> v10 = decodeSerializableElement(descriptor, 9, f10.serializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Failed to decode element")
                    }
                }
            }
        }

        return constructor(v1 as T1, v2 as T2, v3 as T3, v4 as T4, v5 as T5, v6 as T6, v7 as T7, v8 as T8, v9 as T9, v10 as T10)
    }

    override fun serialize(encoder: Encoder, value: S) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, f1.serializer, f1.getter(value))
            encodeSerializableElement(descriptor, 1, f2.serializer, f2.getter(value))
            encodeSerializableElement(descriptor, 2, f3.serializer, f3.getter(value))
            encodeSerializableElement(descriptor, 3, f4.serializer, f4.getter(value))
            encodeSerializableElement(descriptor, 4, f5.serializer, f5.getter(value))
            encodeSerializableElement(descriptor, 5, f6.serializer, f6.getter(value))
            encodeSerializableElement(descriptor, 6, f7.serializer, f7.getter(value))
            encodeSerializableElement(descriptor, 7, f8.serializer, f8.getter(value))
            encodeSerializableElement(descriptor, 8, f9.serializer, f9.getter(value))
            encodeSerializableElement(descriptor, 9, f10.serializer, f10.getter(value))
        }
    }
}


public data class KStructureComponent<S, T>(
    val key: String,
    val serializer: KSerializer<T>,
    val getter: (S) -> T
)