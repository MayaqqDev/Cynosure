package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.client.models.entity.AnimationDataLoader
import dev.mayaqq.cynosure.client.models.entity.ModelDataLoader
import dev.mayaqq.cynosure.client.splash.data.CynosureSplashLoader
import dev.mayaqq.cynosure.modId
import invoke.kitty.kritter.platform.forge.EntrypointHandler
import invoke.kitty.kritter.resources.registerReloadListener
import net.minecraft.server.packs.PackType

public object CynosureClient {
    public fun init() {}
}

@EntrypointHandler("client")
public fun kritterInit() {
    registerReloadListener(PackType.CLIENT_RESOURCES, modId("data_entity_models"), ModelDataLoader)
    registerReloadListener(PackType.CLIENT_RESOURCES, modId("data_entity_animations"), AnimationDataLoader)
    registerReloadListener(PackType.CLIENT_RESOURCES, modId("splashes"), CynosureSplashLoader)
}