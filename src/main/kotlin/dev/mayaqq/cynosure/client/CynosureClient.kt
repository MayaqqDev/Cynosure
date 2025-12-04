package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.client.events.ClientReloadListenerEvent
import dev.mayaqq.cynosure.client.models.entity.AnimationDataLoader
import dev.mayaqq.cynosure.client.models.entity.ModelDataLoader
import dev.mayaqq.cynosure.client.splash.data.CynosureSplashLoader
import dev.mayaqq.cynosure.core.Environment
import dev.mayaqq.cynosure.data.registerResourcepackReloadListener
import dev.mayaqq.cynosure.events.api.EventSubscriber
import dev.mayaqq.cynosure.events.api.Subscription
import dev.mayaqq.cynosure.modId

@EventSubscriber(env = [Environment.CLIENT])
public object CynosureClient {
    public fun init() {
    }

    @Subscription
    internal fun onReloadListeners(event: ClientReloadListenerEvent) {
        event.register(modId("data_entity_models"), ModelDataLoader)
        event.register(modId("data_entity_animations"), AnimationDataLoader)
        event.register(modId("splashes"), CynosureSplashLoader)
    }
}