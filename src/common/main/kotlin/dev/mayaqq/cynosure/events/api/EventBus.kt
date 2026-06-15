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
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.kotlinFunction
import invoke.kitty.nullevt.Event as NullEvent

public val CynosureEventLogger: Logger = LoggerFactory.getLogger("cynosure/events")

public object MainBus : EventBus("main") {
    init {
        runBlocking {
            CynosureAutoSubscriptionService.SERVICES.asFlow()
                .flatMapConcat { withContext(Dispatchers.IO) { it.gatherEventSubscribers().buffer() } }
                .collect { (mod, target) ->
                    try {
                        subscribe(target)
                    } catch (ex: Exception) {
                        CynosureEventLogger.error("Error subscribing class '${target.name}' of mod '$mod' to main event bus", ex)
                    }
                }
        }
    }
}

@OptIn(ExperimentalContextualCancellation::class)
public open class EventBus(public val name: String) {

    private val events = mutableMapOf<Class<out Event>, NullEvent<(Event) -> Unit>>()

    public fun <E : Event> register(clazz: Class<E>, priority: Int = 0, listener: (E) -> Unit): Any  {
        return getOrCreateEvent(clazz).subscribe(priority, listener as (Event) -> Unit)
    }

    public inline fun <reified E : Event> register(priority: Int = 0, noinline listener: (E) -> Unit): Any =
        register(E::class.java, priority, listener)

    public fun subscribe(instance: Any) {
        val functions = when(val i = if (instance is KClass<*>) instance.java else instance) {
            is Class<*> -> i.declaredMethods.filter { Modifier.isStatic(it.modifiers) }
                .mapNotNull { it.kotlinFunction }
            else -> i::class.declaredMemberFunctions
        }

        for (function in functions) {
            val info = function.findAnnotation<Subscription>() ?: continue
            val eventClass = function.parameters.singleOrNull()?.type?.jvmErasure?.java ?: continue

            if (!Event::class.java.isAssignableFrom(eventClass)) continue
            val event = getOrCreateEvent(eventClass.asSubclass(Event::class.java))
            event.addListener(
                event.createDirectListener(instance, function, info.priority)
                    ?: event.createListener(instance, function as (Event) -> Unit, info.priority)
            )
        }
    }

    public fun unregister(ref: Any) {
        events.values.forEach { it.unsubscribe(ref) }
    }

    public fun post(event: Event): Boolean {
        getOrCreateEvent(event.javaClass).dispatcher(event)
        return (event as? CancellableEvent)?.isCancelled ?: false
    }

    private fun getOrCreateEvent(clazz: Class<out Event>): NullEvent<(Event) -> Unit> =
        events.getOrPut(clazz) {
            val event = if (CancellableEvent::class.java.isAssignableFrom(clazz)) newEvent<(Event) -> Unit, _>(Unit, CANCELLATION_STRATEGY)
            else newEvent<(Event) -> Unit>()

            val superClass = clazz.superclass
            if (superClass != Event::class.java) {
                val superEvent = getOrCreateEvent(superClass.asSubclass(Event::class.java))
                event.subscribe(NullEvent.LOWEST_PRIORITY) { superEvent.dispatcher(it) }
            }

            return event
        }

    override fun toString(): String = name

    public companion object {
        private val CANCELLATION_STRATEGY = CancellationStrategy.Contextual<Unit> { _: Unit, evt: CancellableEvent -> evt.isCancelled }
    }
}