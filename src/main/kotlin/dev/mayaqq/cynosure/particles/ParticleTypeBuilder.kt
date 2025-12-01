package dev.mayaqq.cynosure.particles

import com.mojang.brigadier.StringReader
import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.CynosureInternal
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import uwu.serenity.kritter.api.Registrar
import uwu.serenity.kritter.api.builders.Builder
import uwu.serenity.kritter.api.builders.BuilderCallback
import uwu.serenity.kritter.api.entry.RegistryEntry
import uwu.serenity.kritter.internal.NotUsableInBuilder

public inline fun <T : CynosureParticleOptions<T>> Registrar<ParticleType<*>>.particleType(
    name: String,
    codec: Codec<T>,
    networkCodec: ByteCodec<T>,
    builder: CynosureParticleTypeBuilder<T>.() -> Unit = {}
) : RegistryEntry<CynosureParticleType<T>> {
    return CynosureParticleTypeBuilder(name, this, getCallback(), codec, networkCodec).apply(builder)
        .register()
}

public inline fun Registrar<ParticleType<*>>.particleType(
    name: String,
    builder: SimpleParticleTypeBuilder.() -> Unit = {}
) : RegistryEntry<SimpleParticleType> {
    return SimpleParticleTypeBuilder(name, this, getCallback()).apply(builder).register()
}

public interface ParticleTypeBuilder<T : ParticleOptions> {

    @NotUsableInBuilder
    @CynosureInternal
    public fun getResult(): ParticleType<T>
}

public class CynosureParticleTypeBuilder<T : CynosureParticleOptions<T>>(
    name: String,
    owner: Registrar<ParticleType<*>>,
    callback: BuilderCallback<ParticleType<*>, CynosureParticleType<T>>,
    public val codec: Codec<T>,
    public val networkCodec: ByteCodec<T>
) : Builder<ParticleType<*>, CynosureParticleType<T>>(name, owner, callback), ParticleTypeBuilder<T> {

    public var overrideLimiter: Boolean = false

    public lateinit var fromCommand: (StringReader) -> T

    @NotUsableInBuilder
    @CynosureInternal
    override fun getResult(): ParticleType<T> = result!!.value

    override fun createEntry(): CynosureParticleType<T> {
        if (::fromCommand.isInitialized) {
            val fromCommand = fromCommand
            return object : CynosureParticleType<T>(codec, networkCodec, overrideLimiter) {
                override fun fromCommand(reader: StringReader): T = fromCommand(reader)
            }
        } else return CynosureParticleType(codec, networkCodec, overrideLimiter)
    }
}

public class SimpleParticleTypeBuilder(
    name: String,
    owner: Registrar<ParticleType<*>>,
    callback: BuilderCallback<ParticleType<*>, SimpleParticleType>
) : Builder<ParticleType<*>, SimpleParticleType>(name, owner, callback), ParticleTypeBuilder<SimpleParticleType> {

    public var overrideLimiter: Boolean = false

    @NotUsableInBuilder
    @CynosureInternal
    override fun getResult(): ParticleType<SimpleParticleType> = result!!.value

    override fun createEntry(): SimpleParticleType = SimpleParticleType(overrideLimiter)
}