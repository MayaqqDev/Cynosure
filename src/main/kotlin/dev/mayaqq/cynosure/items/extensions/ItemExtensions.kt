package dev.mayaqq.cynosure.items.extensions

import dev.mayaqq.cynosure.core.extensions.Extension
import dev.mayaqq.cynosure.core.extensions.ExtensionRegistry
import dev.mayaqq.cynosure.items.extensions.ItemExtension.Registry.register
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

/**
 * ItemExtensions includes a set of interfaces that extend normal item's functionality. These can be either
 * implemented by implementing the interface on the item class, or by registering an extension using [register]
 */
public interface ItemExtension : Extension<Item> {

    /**
     * Companion object providing registry functionality
     */
    public companion object Registry : ExtensionRegistry<Item, ItemExtension>(Item::class.java, ItemExtension::class.java)
}

public fun <E : ItemExtension> Item.registerExtension(extension: E, allowOverride: Boolean = false) {
    ItemExtension.register(this, extension, allowOverride)
}

public inline fun <reified E : ItemExtension> Item.getExtension(): E? =
    ItemExtension.getExtension(E::class.java, this)


/**
 * Give this item the ability to be traded with piglins
 */
public fun interface BarterCurrency : ItemExtension {
    public fun isBarterCurrency(stack: ItemStack): Boolean
}

/**
 * Makes an item make piglins neutral
 */
public interface PiglinNeutralizing : ItemExtension {
    public fun makesPiglinsNeutral(entity: LivingEntity, stack: ItemStack): Boolean
}

/**
 * An extension providing the ability to listen to item entity ticks
 */
public fun interface ItemEntityTickListener : ItemExtension {
    public fun tickItemEntity(stack: ItemStack, entity: ItemEntity): Boolean
}

/**
 * An extension providing itemstack dependant furnace burn times.
 * Returning null leaves the original value unchanged
 */
public fun interface CustomFurnaceFuel : ItemExtension {
    public fun getItemBurnTime(stack: ItemStack, recipeType: RecipeType<*>?): Int?
}

/**
 * Allows creating a custom entity for this item
 */
public fun interface CustomEntityItem : ItemExtension {
    public fun createItemEntity(original: ItemEntity, level: Level, stack: ItemStack): Entity
}

/**
 * Disables the cape rendering when equipped on the chest
 */
public fun interface DisablesCape : ItemExtension {
    public fun disablesCape(itemStack: ItemStack, player: AbstractClientPlayer): Boolean
}

/**
 * Allows adding custom lines to the item tooltip
 */
public fun interface CustomTooltip : ItemExtension {
    public fun MutableList<Component>.modifyTooltip(stack: ItemStack, player: Player?, flags: TooltipFlag)
}

