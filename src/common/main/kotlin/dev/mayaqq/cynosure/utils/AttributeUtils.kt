package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.core.VersionHooks
import dev.mayaqq.cynosure.internal.CynosureHooks
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeSupplier

public fun AttributeSupplier.toBuilder(): AttributeSupplier.Builder = CynosureHooks.attributeSupplierToBuilder(this)

public fun AttributeSupplier.Builder.add(attribute: Attribute): Unit = VersionHooks.addToAttributeBuilder(this, attribute)