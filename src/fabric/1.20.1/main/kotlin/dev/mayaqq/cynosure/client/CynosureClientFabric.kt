package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.client.events.CynosureWorldRenderEventHandler
import dev.mayaqq.cynosure.client.events.render.EndHudRenderEvent
import dev.mayaqq.cynosure.events.api.post
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.Minecraft

public object CynosureClientFabric {
    @CynosureInternal
    public fun init() {
        CommonCynosureClientFabric.init()
        CynosureWorldRenderEventHandler.init()

        HudRenderCallback.EVENT.register { graphics, float ->
            EndHudRenderEvent(Minecraft.getInstance().gui, graphics, float).post()
        }
    }
}