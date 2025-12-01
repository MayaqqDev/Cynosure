package dev.mayaqq.cynosure.particles

import com.mojang.brigadier.StringReader
import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.Cynosure
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.TagParser
import net.minecraft.network.FriendlyByteBuf

@Suppress("UNCHECKED_CAST")
public open class CynosureParticleType<T : CynosureParticleOptions<T>>(
    public val codec: Codec<T>,
    public val networkCodec: ByteCodec<T>,
    overrideLimiter: Boolean = false
) : ParticleType<T>(overrideLimiter, EnhancedDeserializer as ParticleOptions.Deserializer<T>) {

    final override fun getDeserializer(): ParticleOptions.Deserializer<T> = super.getDeserializer()

    final override fun codec(): Codec<T> = codec

    protected open fun fromCommand(reader: StringReader): T {
        reader.expect(' ')
        return codec.parse(NbtOps.INSTANCE, TagParser(reader).readValue())
            .getOrThrow(false, Cynosure::error)
    }

    private object EnhancedDeserializer : ParticleOptions.Deserializer<CynosureParticleOptions<*>> {

        override fun fromCommand(p0: ParticleType<CynosureParticleOptions<*>>, p1: StringReader): CynosureParticleOptions<*> {
            return (p0 as CynosureParticleType<*>).fromCommand(p1)
        }

        override fun fromNetwork(p0: ParticleType<CynosureParticleOptions<*>>, p1: FriendlyByteBuf): CynosureParticleOptions<*> {
            return (p0 as CynosureParticleType<*>).networkCodec.decode(p1)
        }
    }
}

public interface CynosureParticleOptions<S : CynosureParticleOptions<S>> : ParticleOptions {

    override fun getType(): CynosureParticleType<S>

    @Suppress("UNCHECKED_CAST")
    override fun writeToNetwork(p0: FriendlyByteBuf) {
        type.networkCodec.encode(this as S, p0)
    }

    override fun writeToString(): String = toString()
}