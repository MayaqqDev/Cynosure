package dev.mayaqq.cynosure.helpers

import dev.mayaqq.cynosure.client.enviroment.ClientOnly
import dev.mayaqq.cynosure.client.enviroment.ClientOnlyException
import invoke.kitty.kritter.platform.ENVIRONMENT
import invoke.kitty.kritter.platform.Side
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import java.util.UUID

@ClientOnly
public val McPlayer: Player? get() = if (ENVIRONMENT == Side.CLIENT) McClient.player else throw ClientOnlyException()

public val Player.name: String get() = this.gameProfile.name
public val Player.uuid: UUID get() = this.gameProfile.id

public val Player.heldItem: ItemStack get() = this.mainHandItem ?: ItemStack.EMPTY
public val Player.helmet: ItemStack get() = this.getItemBySlot(EquipmentSlot.HEAD) ?: ItemStack.EMPTY
public val Player.chestplate: ItemStack get() = this.getItemBySlot(EquipmentSlot.CHEST) ?: ItemStack.EMPTY
public val Player.leggings: ItemStack get() = this.getItemBySlot(EquipmentSlot.LEGS) ?: ItemStack.EMPTY
public val Player.boots: ItemStack get() = this.getItemBySlot(EquipmentSlot.FEET) ?: ItemStack.EMPTY