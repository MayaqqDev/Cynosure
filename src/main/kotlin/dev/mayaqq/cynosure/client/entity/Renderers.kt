package dev.mayaqq.cynosure.client.entity

import dev.mayaqq.cynosure.core.AccessWidenerReplacement
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

@AccessWidenerReplacement
public fun <T : Entity> registerEntityRenderer(entity: EntityType<T>, renderer: EntityRendererProvider<in T>) {
    EntityRenderers.register(entity, renderer)
}

@AccessWidenerReplacement
public fun <T : BlockEntity> registerBlockEntityRenderer(blockEntity: BlockEntityType<T>, renderer: BlockEntityRendererProvider<in T>) {
    BlockEntityRenderers.register(blockEntity, renderer)
}