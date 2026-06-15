package dev.mayaqq.cynosure.events.api

/**
 * Base interface for all events
 */
public abstract class Event

/**
 * Annotation marking an event class as a base class. This prevents people from subscribing to events of this class directly
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
public annotation class RootEventClass

/**
 * Event base class with a simple cancelling implementation
 */
@RootEventClass
public abstract class CancellableEvent : Event() {

    public open var isCancelled: Boolean = false
        protected set

    public fun cancel() {
        isCancelled = true
    }
}

/**
 * Event base class with a simple result implementation
 */
@RootEventClass
public abstract class ReturningEvent<R> : CancellableEvent() {
    public var result: R? = null
        set(new) {
            requireNotNull(new) { "Cannot unset the result of the event" }
            field = new
        }

    override var isCancelled: Boolean
        get() = result !== null
        set(_) {}
}



/**
 * Post this event to the event bus, defaults to the [MainBus]
 */
public fun Event.post(bus: EventBus = MainBus) : Boolean {
    return bus.post(this)
}

public fun <R> ReturningEvent<R>.post(bus: EventBus = MainBus): R? {
    bus.post(this)
    return result
}
