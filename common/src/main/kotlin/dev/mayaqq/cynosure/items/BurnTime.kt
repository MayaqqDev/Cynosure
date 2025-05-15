package dev.mayaqq.cynosure.items

import dev.mayaqq.cynosure.internal.CynosureHooks
import net.minecraft.world.item.Item


public var Item.burnTime: Int
    get() = CynosureHooks.getItemBurnTime(defaultInstance)
    set(value) { CynosureHooks.registerBurnTime(this, value) }