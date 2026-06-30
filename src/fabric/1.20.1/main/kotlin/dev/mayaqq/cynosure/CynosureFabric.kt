package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.client.CynosureClientFabric
import dev.mayaqq.cynosure.events.versionFapiFeed
import invoke.kitty.kritter.utils.clientOnly

fun init() {
    CommonCynosureFabric.init()
    versionFapiFeed()

    clientOnly {
        CynosureClientFabric.init()
    }
}