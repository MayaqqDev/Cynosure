package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.core.VersionHooks
import dev.mayaqq.cynosure.internal.CynosureHooks
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeSupplier

public fun AttributeSupplier.toBuilder(): AttributeSupplier.Builder = CynosureHooks.attributeSupplierToBuilder(this)

public fun AttributeSupplier.Builder.add(attribute: Attribute): AttributeSupplier.Builder = VersionHooks.addToAttributeBuilder(this, attribute)

public fun ServerPlayer.getAttribute(attribute: Attribute): AttributeInstance? = VersionHooks.getPlayerAttribute(this, attribute)