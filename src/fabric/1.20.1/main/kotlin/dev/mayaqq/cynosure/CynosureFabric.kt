package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.events.LateInitEvent
import dev.mayaqq.cynosure.events.api.post
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.mixin.client.rendering.WorldRendererMixin

public object CynosureFabric {
    public fun init() {
        Cynosure.init()
    }

    public fun lateinit() {
        LateInitEvent.post()
    }
}