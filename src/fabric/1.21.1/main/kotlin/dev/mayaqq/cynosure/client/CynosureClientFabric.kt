package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.client.events.CynosureWorldRenderEventHandler
import dev.mayaqq.cynosure.client.events.render.EndHudRenderEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.helpers.McClient
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.Minecraft
import net.minecraft.core.RegistryAccess
import java.util.Objects


public object CynosureClientFabric {
    @CynosureInternal
    public fun init() {
        CommonCynosureClientFabric.init()
        CynosureWorldRenderEventHandler.init()

        HudRenderCallback.EVENT.register { graphics, delta ->
            EndHudRenderEvent(Minecraft.getInstance().gui, graphics, delta.getGameTimeDeltaPartialTick(false)).post()
        }
    }

    public fun getRegistryAccess(): RegistryAccess {
        Objects.requireNonNull(McClient.connection, "Client is not connected to a server")
        return McClient.connection!!.registryAccess()
    }
}