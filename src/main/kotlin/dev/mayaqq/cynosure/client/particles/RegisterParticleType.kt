package dev.mayaqq.cynosure.client.particles

import dev.mayaqq.cynosure.client.events.ParticleFactoryRegistrationEvent
import dev.mayaqq.cynosure.events.api.MainBus
import dev.mayaqq.cynosure.particles.ParticleTypeBuilder
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import net.minecraft.core.particles.ParticleOptions
import uwu.serenity.kritter.client.stdlib.clientOnly
import uwu.serenity.kritter.internal.NotUsableInBuilder

public typealias SafeParticleFactory<T> = (T, ClientLevel, x: Double, y: Double, z: Double, xSpeed: Double, ySpeed: Double, zSpeed: Double) -> Particle

@OptIn(NotUsableInBuilder::class)
public inline fun <T : ParticleOptions> ParticleTypeBuilder<T>.provider(crossinline factory: SafeParticleFactory<T>) {
    clientOnly {
        MainBus.register<ParticleFactoryRegistrationEvent> {
            it.register(this.getResult()) { type, level, x, y, z, xs, ys, zs ->
                factory(type, level, x, y, z, xs, ys, zs)
            }
        }
    }
}

@OptIn(NotUsableInBuilder::class)
public inline fun <T : ParticleOptions> ParticleTypeBuilder<T>.provider(crossinline factory: (SpriteSet) -> ParticleProvider<T>) {
    clientOnly {
        MainBus.register<ParticleFactoryRegistrationEvent> {
            it.register(this.getResult()) { spriteSet -> factory(spriteSet) }
        }
    }
}