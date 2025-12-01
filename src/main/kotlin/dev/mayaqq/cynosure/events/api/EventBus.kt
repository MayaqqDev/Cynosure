package dev.mayaqq.cynosure.events.api

import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.utils.asm.descriptorToClassName
import dev.mayaqq.cynosure.utils.asm.mappedValues
import org.objectweb.asm.tree.ClassNode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.function.Consumer
import kotlin.metadata.ClassKind
import kotlin.metadata.jvm.KotlinClassMetadata
import kotlin.metadata.jvm.Metadata
import kotlin.metadata.kind
import kotlin.reflect.KClass

/**
 * Main event bus where all minecraft events are run
 */
public object MainBus : EventBus("main")

/**
 * Subscribe to an event bus. If the receiver is an instance or object any instance method marked with [Subscription]
 * will be added to the event bus. If the receiver is a [KClass] or [Class] instance, any static methods of the class
 * will be added to the event bus
 */
@JvmSynthetic
public fun Any.subscribeTo(bus: EventBus) {
    bus.subscribe(this)
}

/**
 * Unsubscribe from an event bus
 */
@JvmSynthetic
public fun Any.unsubscribeFrom(bus: EventBus) {
    bus.unsubscribe(this)
}


public abstract class EventBus(private val id: String) {

    private val listeners: MutableMap<Class<*>, EventListeners> = mutableMapOf()
    private val handlers: MutableMap<Class<*>, Consumer<Any>> = mutableMapOf()

    public inline fun <reified E : Event> register(priority: Int = 0, receiveCancelled: Boolean = false, noinline handler: (E) -> Unit) {
        register(E::class.java, priority, receiveCancelled, handler)
    }

    @JvmOverloads
    public fun <E : Event> register(clazz: Class<E>, priority: Int = 0, receiveCancelled: Boolean = false, handler: (E) -> Unit) {
        unregisterHandler(clazz)
        listeners.getOrPut(clazz, ::EventListeners).addLambdaListener(clazz, handler, priority, receiveCancelled)
    }

    public fun subscribe(instance: Any) {
        val thing = if(instance is KClass<*>) instance.java else instance
        if(thing is Class<*>) {
            thing.declaredMethods.filter { Modifier.isStatic(it.modifiers) }
                .forEach { registerMethod(it) }
        } else {
            thing.javaClass.declaredMethods.filter { !Modifier.isStatic(it.modifiers) }
                .forEach { registerMethod(it, thing) }
        }
    }

    public inline fun <reified E : Event> unregister(noinline callback: (E) -> Unit) {
        unregister(E::class.java, callback = callback)
    }

    public fun <E : Event> unregister(type: Class<E>, callback: (E) -> Unit) {
        unregisterHandler(type)
        listeners.values.forEach { it.removeListener(callback) }
    }

    public fun unsubscribe(instance: Any) {
        val thing = if(instance is KClass<*>) instance.java else instance
        if(thing is Class<*>) {
            thing.declaredMethods.filter { Modifier.isStatic(it.modifiers) }
                .forEach { unregisterMethod(it) }
        } else {
            thing.javaClass.declaredMethods.filter { !Modifier.isStatic(it.modifiers) }
                .forEach { unregisterMethod(it) }
        }
    }

    @Deprecated("context and onError don't do anything anymore", replaceWith = ReplaceWith("post"))
    public fun post(
        event: Event,
        context: Any? = null,
        onError: ((Throwable) -> Unit)? = null
    ): Boolean = post(event)

    public fun post(
        event: Event,
    ): Boolean {
        getHandler(event.javaClass).accept(event)
        return event.isCancelled
    }

    @CynosureInternal
    public fun registerClassNode(classNode: ClassNode) {

        val metadata = classNode.visibleAnnotations
            ?.find { it.desc.descriptorToClassName() == Metadata::class.qualifiedName }
            ?.mappedValues
            ?.let {
                Metadata(
                    kind = it["k"] as? Int,
                    metadataVersion = (it["mv"] as? List<Int>)?.toIntArray(),
                    data1 = (it["d1"] as? List<String>)?.toTypedArray(),
                    data2 = (it["d2"] as? List<String>)?.toTypedArray(),
                    extraString = it["xs"] as? String,
                    packageName = it["pn"] as? String,
                    extraInt = it["xi"] as? Int
                )
            }
            ?.let(KotlinClassMetadata::readLenient)

        val instanceFieldName: String?
        val instanceFieldOwner: String?

        if (metadata is KotlinClassMetadata.Class) {
            val klass = metadata.kmClass
            when (klass.kind) {
                ClassKind.OBJECT -> {
                    instanceFieldOwner = classNode.name
                    instanceFieldName = "INSTANCE"
                }
                ClassKind.COMPANION_OBJECT -> {
                    instanceFieldOwner = classNode.name.substringBeforeLast('\$')
                    instanceFieldName = classNode.name.substringAfterLast('\$')
                }
                else ->  {
                    instanceFieldOwner = null
                    instanceFieldName = null
                }
            }
        } else {
            instanceFieldName = null
            instanceFieldOwner = null
        }

        for (method in classNode.methods) {

            if (method.parameters != null && method.parameters.size != 1) continue

            val options = method.visibleAnnotations
                ?.find { it.desc.descriptorToClassName() == Subscription::class.java.canonicalName }
                ?.mappedValues
                ?: continue

            registerASMMethod(classNode.name, method.name, method.desc, options, instanceFieldName, instanceFieldOwner)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerASMMethod(className: String, methodName: String, methodDesc: String, options: Map<String, Any?>, instanceFieldName: String?, instanceFieldOwner: String?) {

        val priority = options["priority"] as? Int ?: 0
        val receiveCancelled = options["receiveCancelled"] as? Boolean ?: false

        val event = Class.forName(methodDesc.substringAfter("(").substringBefore(")").descriptorToClassName())
        if (!Event::class.java.isAssignableFrom(event)) return
        unregisterHandler(event)
        listeners.getOrPut(event as Class<Event>, ::EventListeners).add(EventListener(
            event, className, methodName, methodDesc,
            instanceFieldName?.let { InvokerType.VirtualWithOwner(it, instanceFieldOwner!!) } ?: InvokerType.Static,
            priority, receiveCancelled, "$className.$methodName$methodDesc"
        ))
    }

    @Suppress("UNCHECKED_CAST")
    private fun registerMethod(method: Method, instance: Any? = null) {
        if (method.parameterCount != 1) return
        val options = method.getAnnotation(Subscription::class.java) ?: return

        val event = method.parameterTypes[0]
        if (!Event::class.java.isAssignableFrom(event)) return
        unregisterHandler(event)
        listeners.getOrPut(event as Class<Event>, ::EventListeners)
            .addMethodListener(event, method, instance, options.priority, options.receiveCancelled)
    }

    private fun unregisterMethod(method: Method) {
        if (method.parameterCount != 1) return
        method.getAnnotation(Subscription::class.java) ?: return

        val event = method.parameterTypes[0]
        if (!Event::class.java.isAssignableFrom(event)) return
        unregisterHandler(event)
        listeners.values.forEach { it.removeListener(method) }
    }

    private fun <T : Event> getHandler(event: Class<T>): Consumer<Any> = handlers[event] ?: getEventClasses(event)
        .flatMap { listeners[it] ?: emptyList() }
        .sortedBy(EventListener::priority)
        .createHandler(event)
        .also { if (!event.isAnnotationPresent(SinglePostEvent::class.java)) handlers[event] = it }

    private fun unregisterHandler(clazz: Class<*>) = this.handlers.keys
        .filter { it.isAssignableFrom(clazz) }
        .forEach(this.handlers::remove)


    private fun getEventClasses(clazz: Class<*>): List<Class<*>> {
        val classes = mutableListOf<Class<*>>()
        classes.add(clazz)

        var current = clazz
        while (current.superclass != null) {
            val superClass = current.superclass
            if (superClass == Any::class.java
                || superClass.getAnnotation(RootEventClass::class.java) != null) break
            classes.add(superClass)
            current = superClass
        }
        return classes
    }

    override fun toString(): String  = id
}

@CynosureInternal
public val CynosureEventLogger: Logger = LoggerFactory.getLogger("Cynosure Event Registration")