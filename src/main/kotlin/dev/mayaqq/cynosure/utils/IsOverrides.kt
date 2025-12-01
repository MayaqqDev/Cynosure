package dev.mayaqq.cynosure.utils

import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import java.util.function.Predicate

public infix fun BlockState.of(block: Block): Boolean = this.`is`(block)
public infix fun BlockState.of(holder: HolderSet<Block>): Boolean = this.`is`(holder)

public infix fun ItemStack.of(item: Item): Boolean = this.`is`(item)
public infix fun ItemStack.of(holder: Holder<Item>): Boolean = this.`is`(holder)
public infix fun ItemStack.of(predicate: Predicate<Holder<Item>>): Boolean = this.`is`(predicate)

public infix fun Entity.of(entity: Entity): Boolean = this.`is`(entity)
public infix fun Entity.of(type: EntityType<*>): Boolean = this.type == type

public infix fun Holder<*>.of(id: ResourceLocation): Boolean = this.`is`(id)
public infix fun <T> Holder<T>.of(key: ResourceKey<T>): Boolean = this.`is`(key)
public infix fun <T> Holder<T>.of(predicate: Predicate<ResourceKey<T>>): Boolean = this.`is`(predicate)

public infix fun MobEffectInstance.of(effect: MobEffect): Boolean = this.effect == effect

public infix fun DamageSource.of(key: ResourceKey<DamageType>): Boolean = this.`is`(key)