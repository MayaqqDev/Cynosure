package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.client.CynosureClientFabric
import dev.mayaqq.cynosure.core.bytecodecs.BytecodecUtilsImpl
import dev.mayaqq.cynosure.events.api.MainBus
import dev.mayaqq.cynosure.events.versionFapiFeed
import invoke.kitty.kritter.utils.clientOnly

fun init() {
    MainBus.subscribe(BytecodecUtilsImpl)
    CommonCynosureFabric.init()
    versionFapiFeed()
    clientOnly {
        CynosureClientFabric.init()
    }
}