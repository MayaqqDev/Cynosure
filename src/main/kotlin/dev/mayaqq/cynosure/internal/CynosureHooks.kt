package dev.mayaqq.cynosure.internal

import dev.mayaqq.cynosure.CynosureInternal
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

@CynosureInternal
public interface CynosureHooks {

    public companion object Impl : CynosureHooks by loadPlatform()

    public fun attributeSupplierToBuilder(supplier: AttributeSupplier): AttributeSupplier.Builder

    public fun registerReloadListener(id: ResourceLocation, listener: PreparableReloadListener)

    public fun registerBurnTime(tag: TagKey<Item>, time: Int)

    public fun registerBurnTime(item: ItemLike, time: Int)

    public fun getItemBurnTime(stack: ItemStack): Int

}