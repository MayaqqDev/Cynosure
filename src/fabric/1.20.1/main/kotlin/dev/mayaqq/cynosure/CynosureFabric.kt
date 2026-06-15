package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.events.versionFapiFeed

internal object CynosureFabric {
    fun init() {
        CommonCynosureFabric.init()
        versionFapiFeed()
    }
}