package dev.mayaqq.cynosure.network.serialization

import com.teamresourceful.bytecodecs.base.ByteCodec
import io.netty.buffer.ByteBuf
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

private val SERIALIZERS: SerializersModule = SerializersModule {

}

@OptIn(ExperimentalSerializationApi::class)
internal class ByteBufEncoder(
    override val serializersModule: SerializersModule,
    private val buf: ByteBuf
) : AbstractEncoder() {

    override fun encodeBoolean(value: Boolean) {
        buf.writeBoolean(value)
    }

    override fun encodeByte(value: Byte) {
        buf.writeByte(value.toInt())
    }

    override fun encodeShort(value: Short) {
        buf.writeShort(value.toInt())
    }

    override fun encodeInt(value: Int) {
        buf.writeInt(value)
    }

    override fun encodeLong(value: Long) {
        buf.writeLong(value)
    }

    override fun encodeFloat(value: Float) {
        buf.writeFloat(value)
    }

    override fun encodeDouble(value: Double) {
        buf.writeDouble(value)
    }

    override fun encodeChar(value: Char) {
        buf.writeChar(value.code)
    }

    fun <T> encodeWithCodec(codec: ByteCodec<T>, value: T) {
        codec.encode(value, buf)
    }

    override fun encodeString(value: String) {
        val length = value.length
        if (length.toUShort() !in 0u.toUShort()..UShort.MAX_VALUE)
            throw UnsupportedOperationException("Maximum encodable length of string is ${UShort.MAX_VALUE}")
        buf.writeShort(length)
        buf.writeBytes(value.toByteArray())
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        buf.writeInt(index)
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        buf.writeShort(collectionSize)
        return this
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal class ByteBufDecoder(
    override val serializersModule: SerializersModule,
    private val buf: ByteBuf
) : AbstractDecoder() {

    private var currentIndex = 0;

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return if (currentIndex < descriptor.elementsCount) currentIndex.also { currentIndex++ }
        else CompositeDecoder.DECODE_DONE
    }

    @ExperimentalSerializationApi
    override fun decodeSequentially(): Boolean = true

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = buf.readShort().toInt()

    override fun decodeBoolean(): Boolean = buf.readBoolean()

    override fun decodeByte(): Byte = buf.readByte()

    override fun decodeShort(): Short = buf.readShort()

    override fun decodeInt(): Int = buf.readInt()

    override fun decodeLong(): Long = buf.readLong()

    override fun decodeFloat(): Float = buf.readFloat()

    override fun decodeDouble(): Double = buf.readDouble()

    override fun decodeChar(): Char = buf.readChar()

    override fun decodeString(): String {
        val length = buf.readShort().toUShort().toInt()
        val data = ByteArray(length)
        buf.readBytes(data)
        return String(data)
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = buf.readInt()

    fun <T> decodeWithCodec(codec: ByteCodec<T>): T = codec.decode(buf)
}

internal fun <T> encodeToBuf(serializer: SerializationStrategy<T>, value: T, buf: ByteBuf) {
    val encoder = ByteBufEncoder(SERIALIZERS, buf)
    serializer.serialize(encoder, value)
}

internal fun <T> decodeFromBuf(deserializer: DeserializationStrategy<T>, buf: ByteBuf): T {
    val decoder = ByteBufDecoder(SERIALIZERS, buf)
    return deserializer.deserialize(decoder)
}
