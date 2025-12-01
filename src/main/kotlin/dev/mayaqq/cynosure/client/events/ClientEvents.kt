package dev.mayaqq.cynosure.client.events

import dev.mayaqq.cynosure.events.api.Event

public sealed class ClientTickEvent : Event {

    /**
     * Fired at the beginning of each client tick
     */
    public data object Begin : ClientTickEvent()

    /**
     * Fired at the end of each client tick
     */
    public data object End : ClientTickEvent()
}
