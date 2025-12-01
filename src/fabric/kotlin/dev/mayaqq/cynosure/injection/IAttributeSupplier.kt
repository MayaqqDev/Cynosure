package dev.mayaqq.cynosure.injection

import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder

public interface IAttributeSupplier {
    fun cynosure_toBuilder(): Builder
}