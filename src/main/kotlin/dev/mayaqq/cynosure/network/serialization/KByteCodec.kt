package dev.mayaqq.cynosure.network.serialization

import com.teamresourceful.bytecodecs.base.ByteCodec
import io.netty.buffer.ByteBuf
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public class KByteCodec<T>(public val serializer: KSerializer<T>) : ByteCodec<T> {
    override fun encode(p0: T, p1: ByteBuf) {
        encodeToBuf(serializer, p0, p1)
    }

    override fun decode(p0: ByteBuf): T = decodeFromBuf(serializer, p0)
}

public class ByteCodecSerializer<T>(public val codec: ByteCodec<T>): KSerializer<T> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("dev.mayaqq.cynosure.network.serialization.ByteCodec", SerialKind.CONTEXTUAL)

    override fun serialize(encoder: Encoder, value: T) {
        if (encoder !is ByteBufEncoder) error("ByteCodec can only encode to bytebufs")
        encoder.encodeWithCodec(codec, value)
    }

    override fun deserialize(decoder: Decoder): T {
        if (decoder !is ByteBufDecoder) error("ByteCodecs can only decode byte bufs")
        return decoder.decodeWithCodec(codec)
    }

}