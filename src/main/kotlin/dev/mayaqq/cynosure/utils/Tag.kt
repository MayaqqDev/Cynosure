package dev.mayaqq.cynosure.utils

import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid

public inline fun itemTag(value: ResourceLocation): TagKey<Item> = Registries.ITEM.tag(value)
public inline fun blockTag(value: ResourceLocation): TagKey<Block> = Registries.BLOCK.tag(value)
public inline fun damageTypeTag(value: ResourceLocation): TagKey<DamageType> = Registries.DAMAGE_TYPE.tag(value)
public inline fun entityTypeTag(value: ResourceLocation): TagKey<EntityType<*>> = Registries.ENTITY_TYPE.tag(value)
public inline fun fluidTag(value: ResourceLocation): TagKey<Fluid> = Registries.FLUID.tag(value)

public fun <T> ResourceKey<Registry<T>>.tag(value: ResourceLocation): TagKey<T> = TagKey.create(this, value)

public operator fun <T : Any> TagKey<in T>.contains(key: ResourceLocation): Boolean =
    ResourceKey.create(this.registry as ResourceKey<out Registry<T>>, key) in this

@Suppress("UNCHECKED_CAST")
public operator fun <T : Any> TagKey<in T>.contains(key: ResourceKey<T>): Boolean {
    val registry = BuiltInRegistries.REGISTRY.get(this.registry.location())!! as Registry<T>
    // Minecraft pls be better at type params i beg
    return (registry.getHolderOrThrow(key) as Holder<T>).`is`(this as TagKey<T>)
}

public operator fun <T : Any> TagKey<in T>.contains(value: T): Boolean {
    val registry = BuiltInRegistries.REGISTRY.get(this.registry.location())!! as Registry<T>
    return (registry.getResourceKey(value).get() as ResourceKey<T>) in this
}

public operator fun TagKey<Block>.contains(state: BlockState): Boolean = state.`is`(this)
public operator fun TagKey<Item>.contains(stack: ItemStack): Boolean = stack.`is`(this)
public operator fun TagKey<EntityType<*>>.contains(entity: EntityType<*>): Boolean = entity.`is`(this)
public operator fun <T> TagKey<T>.contains(holder: Holder<T>): Boolean = holder.`is`(this)
public operator fun TagKey<DamageType>.contains(source: DamageSource): Boolean = source.`is`(this)