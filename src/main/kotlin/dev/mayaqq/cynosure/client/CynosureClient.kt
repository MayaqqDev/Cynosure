package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.client.models.entity.AnimationDataLoader
import dev.mayaqq.cynosure.client.models.entity.ModelDataLoader
import dev.mayaqq.cynosure.client.splash.data.CynosureSplashLoader
import dev.mayaqq.cynosure.data.registerResourcepackReloadListener
import dev.mayaqq.cynosure.modId

public object CynosureClient {
    public fun init() {
        registerResourcepackReloadListener(modId("data_entity_models"), ModelDataLoader)
        registerResourcepackReloadListener(modId("data_entity_animations"), AnimationDataLoader)
        registerResourcepackReloadListener(modId("splashes"), CynosureSplashLoader)
    }
}