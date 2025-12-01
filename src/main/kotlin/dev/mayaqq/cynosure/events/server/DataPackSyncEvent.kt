package dev.mayaqq.cynosure.events.server

import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.world.entity.player.Player

public class DataPackSyncEvent(public val player: Player, public val isJoin: Boolean) : Event