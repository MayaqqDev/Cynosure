package dev.mayaqq.cynosure.events.server

import dev.mayaqq.cynosure.events.api.CancellableEvent
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

public class ServerChatEvent(
    public val player: ServerPlayer,
    public var message: Component,
    public val rawText: String
) : CancellableEvent()