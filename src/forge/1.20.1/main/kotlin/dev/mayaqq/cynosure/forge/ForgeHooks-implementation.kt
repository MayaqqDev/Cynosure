package dev.mayaqq.cynosure.forge

import net.minecraft.world.item.ItemStack

actual object ForgeHooks {
    public actual fun getItemBurnTime(stack: ItemStack): Int = ForgeHooks.getItemBurnTime(stack)
}