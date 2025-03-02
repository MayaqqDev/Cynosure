package dev.mayaqq.cynosure.network

import dev.mayaqq.cynosure.network.base.PacketType

public interface Packet<T : Packet<T>> {
    public val type: PacketType<T>
}