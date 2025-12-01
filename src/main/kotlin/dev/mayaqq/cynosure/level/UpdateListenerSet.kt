package dev.mayaqq.cynosure.level

import it.unimi.dsi.fastutil.objects.ObjectArraySet
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState

internal class UpdateListenerSet(vararg values: BlockUpdateListener) : BlockUpdateListener {

    private val listeners: MutableSet<BlockUpdateListener> = ObjectArraySet(values)

    override val listenedPositions: Iterable<BlockPos>
        get() = throw UnsupportedOperationException("Cant get listened positions of an update listener holder")

    override fun shouldRemove(level: ServerLevel): Boolean {
        if (listeners.isEmpty()) return true
        val iterator = listeners.iterator()
        var shouldRemove = true
        while (iterator.hasNext()) {
            val listener = iterator.next()
            if (listener.shouldRemove(level)) iterator.remove() else shouldRemove = false
        }
        return shouldRemove
    }

    override fun onBlockUpdate(level: ServerLevel, pos: BlockPos, state: BlockState) {
        listeners.forEach {
            it.onBlockUpdate(level, pos, state)
        }
    }

    @JvmName("add\$cynosure")
    internal fun add(listener: BlockUpdateListener) {
        listeners.add(listener)
    }
}