package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.CynosureFabric
import dev.mayaqq.cynosure.client.events.CynosureWorldRenderEventHandler
import dev.mayaqq.cynosure.client.events.render.WorldRenderEvent
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents


public object CynosureClientFabric {
    public fun init() {
        CynosureFabric.lateinit()
        CynosureWorldRenderEventHandler.init()
        CynosureClient.init()
    }
}