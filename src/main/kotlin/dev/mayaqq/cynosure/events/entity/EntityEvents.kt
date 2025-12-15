package dev.mayaqq.cynosure.events.entity

import dev.mayaqq.cynosure.events.api.CancellableEvent
import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.ReturningEvent
import dev.mayaqq.cynosure.events.api.RootEventClass
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

public sealed class EntityTrackingEvent(
    public val entity: Entity,
    public val player: ServerPlayer
) : Event {
    /**
     * Invoked when an entity starts tracking the player
     */
    public class Start(entity: Entity, player: ServerPlayer) : EntityTrackingEvent(entity, player)

    /**
     * Invoked when an entity stops tracking the player
     */
    public class Stop(entity: Entity, player: ServerPlayer) : EntityTrackingEvent(entity, player)
}

@RootEventClass
public sealed class LivingEntityEvent(public val entity: LivingEntity) : CancellableEvent() {

    public class Tick(entity: LivingEntity) : LivingEntityEvent(entity)

    public class Death(entity: LivingEntity, public val source: DamageSource) : LivingEntityEvent(entity)

    public class EffectApply(entity: LivingEntity, public val newInstance: MobEffectInstance, public val oldInstance: MobEffectInstance?, public val effect: MobEffect = newInstance.effect): LivingEntityEvent(entity)
    public class EffectRemove(public val entity: LivingEntity, public val instance: MobEffectInstance?, public val effect: MobEffect? = instance?.effect): CancellableEvent()
    public class EffectExpire(entity: LivingEntity, public val instance: MobEffectInstance?, public val effect: MobEffect? = instance?.effect) : LivingEntityEvent(entity)
}

public class MountEvent(public val entity: Entity, public val mount: Entity?, public val isMounting: Boolean) : CancellableEvent()

public class EntityDamageSourceEvent(public val entity: Entity, public val source: DamageSource) : ReturningEvent<DamageSource>()

public class EntityDamageEvent(public val entity: Entity, public val source: DamageSource, public val amount: Float) : ReturningEvent<Float>()

public class EntityCreatedEvent(public val entity: Entity, public val level: Level) : CancellableEvent()