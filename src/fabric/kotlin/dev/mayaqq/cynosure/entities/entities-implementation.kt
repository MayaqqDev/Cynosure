package dev.mayaqq.cynosure.entities

import dev.mayaqq.cynosure.utils.toBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.DefaultAttributes

/**
 * Utilities for registering and modifying entity attributes
 */
internal class EntityAttributesImpl : EntityAttributes {

    /**
     * Register attributes for this entity type. This will crash if the entitty already has attributes
     * @param supplier the entity attribute supplier
     * @throws IllegalStateException if the entity type already has attributes registered
     */
    override fun register(
        entity: EntityType<out LivingEntity>,
        supplier: AttributeSupplier,
    ) {
        require(!DefaultAttributes.hasSupplier(entity)) { "Cannot register an entity attribute supplier twice" }
        FabricDefaultAttributeRegistry.register(entity, supplier)
    }

    /**
     * Add an attribute to this entity
     * @param attribute The attribute to add
     * @param value The value of the attribute, defaults to [Attribute.defaultValue]
     */
    override fun addToEntity(
        entity: EntityType<out LivingEntity>,
        attribute: Attribute,
        value: Double
    ) {
        val builder = entity.getAttributeBuilder()
        builder.add(attribute, value)
        FabricDefaultAttributeRegistry.register(entity, builder)
    }

    /**
     * Add all the given attributes to this entity
     * @param builder the builder u can modify
     */
    override fun modify(
        entity: EntityType<out LivingEntity>,
        builder: AttributeSupplier.Builder.() -> Unit
    ) {
        val b = entity.getAttributeBuilder()
        b.builder()
        FabricDefaultAttributeRegistry.register(entity, b)
    }

    @PublishedApi
    internal fun EntityType<out LivingEntity>.getAttributeBuilder(): AttributeSupplier.Builder =
        if(DefaultAttributes.hasSupplier(this)) DefaultAttributes.getSupplier(this).toBuilder()
        else AttributeSupplier.builder()
}
