package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.events.api.MainBus

internal object TempClientCynosureEvents {
    fun subAll() {
        MainBus.subscribe(CynosureClient)
    }
}