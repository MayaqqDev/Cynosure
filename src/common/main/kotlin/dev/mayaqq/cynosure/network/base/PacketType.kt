package dev.mayaqq.cynosure.network.base

import dev.mayaqq.cynosure.network.Packet
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

public interface PacketType<T : Packet<T>> {
    public val type: Class<T>

    public val id: ResourceLocation

    public fun encode(packet: T, buffer: FriendlyByteBuf)

    public fun decode(buffer: FriendlyByteBuf): T
}