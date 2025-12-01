package dev.mayaqq.cynosure.events.api

/**
 * Base interface for all events
 * [isCancelled] returns true if the event should be considered cancelled and stop execution
 */
public interface Event {
    /**
     * Return true if this event is cancelled, to make an event cancellable override this value and make it
     * mutable
     */
    public val isCancelled: Boolean get() = false
}

/**
 * Annotation marking an event class as a base class. This prevents people from subscribing to events of this class directly
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class RootEventClass

/**
 * Annotation marking an event as single-post. This prevents retaining of the event handler after the event is fired.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class SinglePostEvent

/**
 * Event base class with a simple cancelling implementation
 */
@RootEventClass
public abstract class CancellableEvent : Event {

    final override var isCancelled: Boolean = false
        private set

    public fun cancel() {
        isCancelled = true
    }
}

/**
 * Event base class with a simple result implementation
 */
@RootEventClass
public abstract class ReturningEvent<R> : Event {
    public var result: R? = null
        set(new) {
            requireNotNull(new) { "Cannot unset the result of the event" }
            field = new
        }

    override val isCancelled: Boolean
        get() = result !== null
}

/**
 * Post this event to the event bus, defaults to the [MainBus]
 */
@Deprecated(message = "meow")
public fun Event.post(bus: EventBus = MainBus, context: Any? = null, onError: ((Throwable) -> Unit)? = null) : Boolean {
    return bus.post(this, context, onError)
}

/**
 * Post this event to the event bus, defaults to the [MainBus]
 */
public fun Event.post(bus: EventBus = MainBus) : Boolean {
    return bus.post(this)
}

/**
 * Post this returning event to the event bus and get its result. Defaults to [MainBus]
 */
@Deprecated(message = "meow")
public fun <R> ReturningEvent<R>.post(bus: EventBus = MainBus, context: Any? = null, onError: ((Throwable) -> Unit)? = null): R? {
    bus.post(this, context, onError)
    return result
}

public fun <R> ReturningEvent<R>.post(bus: EventBus = MainBus): R? {
    bus.post(this)
    return result
}
