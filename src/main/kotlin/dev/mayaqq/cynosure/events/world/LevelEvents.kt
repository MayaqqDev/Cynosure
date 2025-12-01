package dev.mayaqq.cynosure.events.world

import dev.mayaqq.cynosure.core.Environment
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

public sealed class LevelEvent(
    public open val level: Level
) : Event {
    public val isClientSide: Boolean
        get() = level.isClientSide

    public val side: Environment
        get() = if(level.isClientSide) Environment.CLIENT else Environment.SERVER

    public class Load(level: Level) : LevelEvent(level)

    public class Unload(level: Level) : LevelEvent(level)

    public class Save(override val level: ServerLevel) : LevelEvent(level)

    public class BeginTick(level: Level) : LevelEvent(level)

    public class EndTick(level: Level) : LevelEvent(level)
}