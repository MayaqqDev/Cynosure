package dev.mayaqq.cynosure.events.api

import dev.mayaqq.cynosure.core.Environment
import kotlin.reflect.KClass

/**
 * Marks a method as a subscription to an event. This will be added to the event bus if you call [subscribeTo] on
 * the object containing this subscription. Alternatively, you can use the [EventSubscriber] on the object or file
 * owning the method to automatically add them to an event bus
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
public annotation class Subscription(
    /**
     * The priority of when the event will be called, lower priority will be called first, see the companion object.
     */
    val priority: Int = 0,

    /**
     * If the event is cancelled & receiveCancelled is true, then the method will still invoke.
     */
    val receiveCancelled: Boolean = false,
) {

    public companion object {
        public const val HIGHEST: Int = Int.MIN_VALUE
        public const val HIGH: Int = -100000
        public const val LOW: Int = 100000
        public const val LOWEST: Int = Int.MAX_VALUE
    }
}

/**
 * Automatically adds all methods in this object or file to the event bus passed in [bus]
 *
 * Note: On fabric you have to set `"cynosure:autosubscription": true` in your fabric.mod.json custom properties for
 * automatic subscribers to work. This will tell cynosure to scan your mod's classpath for subscribers. If running in a dev
 * environment, also make soure `-Dfabric.classPathGroups` is set correctly
 *
 * Note 2: An event bus has to be an object to be used in this annotation
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FILE, AnnotationTarget.CLASS)
public annotation class EventSubscriber(

    /**
     * Environment in which this event will be registered
     */
    vararg val env: Environment = [Environment.SERVER, Environment.CLIENT],

    /**
     * Event bus object to add the subscriptions to. Defaults to [MainBus]
     */
    val bus: KClass<out EventBus> = MainBus::class

)