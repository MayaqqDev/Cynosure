package dev.mayaqq.cynosure.events.api

import invoke.kitty.kritter.platform.Side
import kotlin.reflect.KClass

/**
 * Marks a method as a subscription to an event. This will be added to the event bus if you call [subscribeTo] on
 * the object containing this subscription. Alternatively, you can use the [EventSubscriber] on the object or file
 * owning the method to automatically add them to an event bus
 */
public typealias Subscription = uwu.serenity.nullbus.Subscription

/**
 * Automatically adds all methods in this object or file to the event bus passed in [bus]
 *
 * Note: On fabric you have to set `"cynosure:autosubscription": true` in your fabric.mod.json custom properties for
 * automatic subscribers to work. This will tell cynosure to scan your mod's classpath for subscribers. If running in a dev
 * environment, also make soure `-Dfabric.classPathGroups` is set correctly
 *
 * Note 2: An event bus has to be an object to be used in this annotation
 */
@Deprecated(message = "Not gonna be used anymore, also gonna move away from bus soon")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FILE, AnnotationTarget.CLASS)
public annotation class EventSubscriber(

    /**
     * Environment in which this event will be registered
     */
    vararg val value: Side = [Side.SERVER, Side.CLIENT]
)