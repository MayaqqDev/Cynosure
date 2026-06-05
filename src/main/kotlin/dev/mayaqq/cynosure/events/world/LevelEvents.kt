package dev.mayaqq.cynosure.events.world

import dev.mayaqq.cynosure.events.api.Event
import invoke.kitty.kritter.platform.Side
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

public sealed class LevelEvent(
    public open val level: Level
) : Event() {
    public val isClientSide: Boolean
        get() = level.isClientSide

    public val side: Side
        get() = if(level.isClientSide) Side.CLIENT else Side.SERVER

    public class Load(level: Level) : LevelEvent(level)

    public class Unload(level: Level) : LevelEvent(level)

    public class Save(override val level: ServerLevel) : LevelEvent(level)

    public class BeginTick(level: Level) : LevelEvent(level)

    public class EndTick(level: Level) : LevelEvent(level)
}