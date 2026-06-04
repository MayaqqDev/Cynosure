package dev.mayaqq.cynosure.events.api

import dev.mayaqq.cynosure.utils.loadService
import invoke.kitty.kritter.platform.Mod
import invoke.kitty.kritter.platform.entrypoint.getEntrypoints
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Method

public interface CynosureAutoSubscriptionService {

    public val order: Int get() = 0

    public fun gatherEventSubscribers(): Flow<SubscriberData>

    @JvmRecord
    public data class SubscriberData(val mod: Mod?, val target: Class<*>)

    public companion object {
        internal val SERVICES: List<CynosureAutoSubscriptionService> =
                loadService<CynosureAutoSubscriptionService>().sortedBy { it.order } +
                getEntrypoints<CynosureAutoSubscriptionService>("cynosure:auto_subscription_service")
                    .mapNotNull { it.resolveClassInstance().getOrNull() }

    }
}