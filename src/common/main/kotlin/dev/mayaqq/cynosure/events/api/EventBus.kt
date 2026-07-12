package dev.mayaqq.cynosure.events.api

import invoke.kitty.nullevt.api.CancellationStrategy
import invoke.kitty.nullevt.api.ExperimentalContextualCancellation
import invoke.kitty.nullevt.newEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.minecraft.Util
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction
import invoke.kitty.nullevt.Event as NullEvent

public val CynosureEventLogger: Logger = LoggerFactory.getLogger("cynosure/events")

public object MainBus : EventBus("main")

@OptIn(ExperimentalContextualCancellation::class)
public open class EventBus(public val name: String) {

    private val innerBus: uwu.serenity.nullbus.EventBus = uwu.serenity.nullbus.EventBus(name)

    public fun <E : Event> register(clazz: Class<E>, priority: Int = 0, listener: (E) -> Unit)  {
        innerBus.subscribe(clazz, priority, false, listener)
    }

    public inline fun <reified E : Event> register(priority: Int = 0, noinline listener: (E) -> Unit) =
        register(E::class.java, priority, listener)

    public fun subscribe(instance: Any) {
        innerBus.subscribe(instance)
    }

    public fun unregister(ref: Any) {
        innerBus.unsubscribe(ref)
    }

    public fun post(event: Event): Boolean {
        return innerBus.post(event)
    }


    override fun toString(): String = name

    public companion object {
        private val CANCELLATION_STRATEGY = CancellationStrategy.Contextual<Unit> { _: Unit, evt: CancellableEvent -> evt.isCancelled }
    }
}