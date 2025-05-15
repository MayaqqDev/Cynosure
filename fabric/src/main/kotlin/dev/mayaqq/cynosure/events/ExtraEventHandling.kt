@file:JvmName("ExtraEventHandling")
package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.core.GameInstanceImpl
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.entity.EntityCreatedEvent
import dev.mayaqq.cynosure.events.entity.MountEvent
import dev.mayaqq.cynosure.items.extensions.CustomEntityItem
import dev.mayaqq.cynosure.items.extensions.getExtension
import dev.mayaqq.cynosure.utils.side
import net.minecraft.server.TickTask
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level

/*
 * Taken from the Forge Event under the lgpl-2.1 license.
 */
internal fun canMountEntity(entityMounting: Entity, entityBeingMounted: Entity?, isMounting: Boolean): Boolean {
    val isCanceled: Boolean = MountEvent(entityMounting, entityBeingMounted, isMounting).post()
    if (isCanceled) {
        entityMounting.absMoveTo(
            entityMounting.x,
            entityMounting.y,
            entityMounting.z,
            entityMounting.yRotO,
            entityMounting.xRotO
        )
        return false
    } else {
        return true
    }
}

internal fun onCreateEntity(level: Level, entity: Entity): Boolean {
    if (entity is ItemEntity) {
        val stack = entity.item
        val extension = stack.item.getExtension<CustomEntityItem>()
        if (extension != null) {
            val newEntity = extension.createItemEntity(entity, level, stack)
            entity.discard()
            GameInstanceImpl.getEventLoop(level.side)
                .tell(TickTask(0) { level.addFreshEntity(newEntity) })
            return false
        }
    }
    return !(EntityCreatedEvent(entity, level).post())
}
