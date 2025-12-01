@file:JvmName("FunctionalTags")
package dev.mayaqq.cynosure.tags

import dev.mayaqq.cynosure.modId
import dev.mayaqq.cynosure.utils.itemTag
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

@JvmField
public val DISABLES_CAPE: TagKey<Item> = itemTag(modId("disables_cape"))