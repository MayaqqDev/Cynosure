package dev.mayaqq.cynosure.network.base

import dev.mayaqq.cynosure.network.Packet
import net.minecraft.server.level.ServerPlayer

public object DummyNetwork : Network {

    override fun <T : Packet<T>> register(type: ClientBoundPacketType<T>) {}

    override fun <T : Packet<T>> register(type: ServerBoundPacketType<T>) {}

    override fun <T : Packet<T>> sendToServer(packet: T) {}

    override fun <T : Packet<T>> sendToClient(packet: T, player: ServerPlayer) {}

    override fun canSendToPlayer(player: ServerPlayer): Boolean {
        return false
    }
}