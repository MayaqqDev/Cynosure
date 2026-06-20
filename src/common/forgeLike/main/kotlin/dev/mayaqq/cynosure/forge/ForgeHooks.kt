package dev.mayaqq.cynosure.forge

import net.minecraft.world.item.ItemStack

expect object ForgeHooks {
    public fun getItemBurnTime(stack: ItemStack): Int
}