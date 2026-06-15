@file:Suppress("ACTUAL_WITHOUT_EXPECT")
package dev.mayaqq.cynosure.entities

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.DefaultAttributes
import net.minecraftforge.common.ForgeHooks

/**
 * Hacking forge lessgooo
 */
internal val ATTRIBUTE_MAP: MutableMap<EntityType<out LivingEntity>, AttributeSupplier> by lazy {
    try {
        val field = ForgeHooks::class.java.getDeclaredField("FORGE_ATTRIBUTES")
        field.isAccessible = true
        return@lazy field.get(null) as MutableMap<EntityType<out LivingEntity>, AttributeSupplier>
    } catch(ex: ReflectiveOperationException) {
        throw ex
    }
}

/**
 * Utilities for registering and modifying entity attributes
 */
public object EntityAttributesImpl : EntityAttributes {

    /**
     * Register attributes for this entity type. This will crash if the entitty already has attributes
     * @param supplier the entity attribute supplier
     * @throws IllegalStateException if the entity type already has attributes registered
     */
    override fun register(
        entity: EntityType<out LivingEntity>,
        supplier: AttributeSupplier,
    ) {
        require(!ATTRIBUTE_MAP.containsKey(entity) && !DefaultAttributes.hasSupplier(entity))
        { "This entity already has registered attributes" }

        ATTRIBUTE_MAP[entity] = supplier
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
        val b = DefaultAttributes.getSupplier(entity).toBuilder()
        b.add(attribute, value)
        ATTRIBUTE_MAP[entity] = b.build()
    }

    /**
     * Add all the given attributes to this entity
     * @param builder the builder u can modify
     */
    override fun modify(
        entity: EntityType<out LivingEntity>,
        builder: AttributeSupplier.Builder.() -> Unit
    ) {
        val b = DefaultAttributes.getSupplier(entity).toBuilder()
        b.builder()
        ATTRIBUTE_MAP[entity] = b.build()
    }

    private fun AttributeSupplier?.toBuilder(): AttributeSupplier.Builder =
        if(this != null) AttributeSupplier.Builder(this) else AttributeSupplier.builder()
}