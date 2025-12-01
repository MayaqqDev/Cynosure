package dev.mayaqq.cynosure.events.entity.player

import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

public sealed class PlayerTickEvent(public val player: Player) : Event {

    public class End(player: Player) : PlayerTickEvent(player)

    public class Begin(player: Player) : PlayerTickEvent(player)
}

public sealed class PlayerConnectionEvent(public val player: ServerPlayer) : Event {

    public class Join(player: ServerPlayer) : PlayerConnectionEvent(player)

    public class Leave(player: ServerPlayer) : PlayerConnectionEvent(player)
}

public class PlayerRestoreEvent(public val player: Player, public val alive: Boolean) : Event