package dev.mayaqq.cynosure.client.events

import com.mojang.blaze3d.vertex.VertexFormat
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.ApiStatus
import kotlin.reflect.KMutableProperty0

public class ParticleFactoryRegistrationEvent(private val context: Context) : Event {

    public fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>) {
        context.register(type, provider)
    }

    public fun <T : ParticleOptions> register(type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>) {
        context.register(type, factoryProvider)
    }

    @ApiStatus.Internal
    @CynosureInternal
    public interface Context {
        public fun <T : ParticleOptions> register(type: ParticleType<T>, provider: ParticleProvider<T>)

        public fun <T : ParticleOptions> register(type: ParticleType<T>, factoryProvider: (SpriteSet) -> ParticleProvider<T>)
    }
}

public class ParticleRenderTypeRegistrationEvent(
    private val renderOrder: MutableList<ParticleRenderType>
) : Event {

    public fun register(type: ParticleRenderType) {
        // TODO: Ordering
        renderOrder.add(type)
    }

    public fun registerOrdered(ordering: Ordering, anchor: ParticleRenderType, type: ParticleRenderType) {
        val index = renderOrder.indexOf(anchor).let { if (ordering == Ordering.AFTER) it + 1 else it }
        renderOrder.add(index, type)
    }

    public enum class Ordering {
        BEFORE, AFTER
    }
}

public class CoreShaderRegistrationEvent(private val context: Context) : Event {

    public fun register(
        id: ResourceLocation,
        format: VertexFormat,
        callback: (ShaderInstance) -> Unit
    ) {
        context.register(id, format, callback)
    }

    public fun register(
        id: ResourceLocation,
        format: VertexFormat,
        property: KMutableProperty0<ShaderInstance>
    ): Unit = register(id, format, property::set)

    @ApiStatus.Internal
    @CynosureInternal
    public fun interface Context {
        public fun register(id: ResourceLocation, format: VertexFormat, onLoad: (ShaderInstance) -> Unit)
    }
}