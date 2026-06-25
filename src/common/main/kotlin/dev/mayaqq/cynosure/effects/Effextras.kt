@file:JvmName("Effextras")
package dev.mayaqq.cynosure.effects

import dev.mayaqq.cynosure.core.VersionHooks
import invoke.kitty.kritter.utils.mapBacked
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity

///**
// * A collection of extra properties for MobEffects.
// */
//public object Effextras {
//    /**
//     * Any Effect in this set will not call onRemove on status effect update.
//     */
//    internal val updateless: MutableSet<MobEffect> = ReferenceOpenHashSet()
//
//    public fun isUpdateless(effect: MobEffect): Boolean = updateless.contains(effect)
//    public fun setUpdateless(effect: MobEffect): Boolean = updateless.add(effect)
//    public fun removeUpdateless(effect: MobEffect): Boolean = updateless.remove(effect)
//}

public var MobEffect.updateless: Boolean by mapBacked(false)

public fun MobEffectInstance.effect(): MobEffect = VersionHooks.getStatusEffect(this)

public fun LivingEntity.hasEffect(effect: MobEffect): Boolean = VersionHooks.hasStatusEffect(this, effect)

public fun newMobEffectInstance(
    effect: MobEffect,
    duration: Int = 0,
    amplifier: Int = 0,
    ambient: Boolean = false,
    visible: Boolean = true,
    showIcon: Boolean = visible,
    hiddenEffect: MobEffectInstance? = null
): MobEffectInstance = VersionHooks.createMobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon, hiddenEffect)