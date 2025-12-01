@file:JvmName("Effextras")
package dev.mayaqq.cynosure.effects

import dev.mayaqq.cynosure.utils.mapBacked
import net.minecraft.world.effect.MobEffect

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