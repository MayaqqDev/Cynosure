package dev.mayaqq.cynosure.events.api

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.Type
import java.lang.reflect.Method
import java.lang.reflect.Modifier

internal typealias EventListeners = ArrayList<EventListener>

private val FUNCTION_1 = Class.forName("kotlin.jvm.functions.Function1")


internal fun EventListeners.removeListener(ref: Any) {
    removeAll { it.ref == ref }
}

internal fun <E : Event> EventListeners.addLambdaListener(event: Class<E>, handler: (E) -> Unit, priority: Int, receiveCancelled: Boolean) {
    add(EventListener(
        event,
        "kotlin/jvm/functions/Function1",
        "invoke",
        "(Ljava/lang/Object;)Ljava/lang/Object;",
        InvokerType.VirtualWithInstance(handler, FUNCTION_1, Opcodes.INVOKEINTERFACE),
        priority, receiveCancelled, handler
    ))
}

internal fun EventListeners.addMethodListener(event: Class<out Event>, method: Method, instance: Any?, priority: Int, receiveCancelled: Boolean) {
     add(EventListener(
         event,
         method.declaringClass.name.replace('.', '/'),
         method.name,
         Type.getMethodDescriptor(method),
         if (Modifier.isStatic(method.modifiers)) InvokerType.Static else InvokerType.VirtualWithInstance(instance!!, method.declaringClass),
         priority, receiveCancelled, method
    ))
}

internal data class EventListener(
    val event: Class<*>,
    val className: String,
    val methodName: String,
    val methodDesc: String,
    val invokerType: InvokerType,
    val priority: Int,
    val receiveCancelled: Boolean,
    val ref: Any? = null
)

internal sealed interface InvokerType {

    data object Static : InvokerType

    data class VirtualWithOwner(
        val ownerClassName: String,
        val ownerFieldName: String,
        val opcode: Int = INVOKEVIRTUAL,
    ) : InvokerType

    data class VirtualWithInstance(
        val instance: Any,
        val clazz: Class<*>,
        val opcode: Int = INVOKEVIRTUAL
    ) : InvokerType {
        override fun equals(other: Any?): Boolean =
            other is VirtualWithInstance
                    && other.instance === this.instance
                    && other.clazz == this.clazz
                    && other.opcode == this.opcode

        override fun hashCode(): Int {
            var result = opcode
            result = 31 * result + System.identityHashCode(instance)
            result = 31 * result + clazz.hashCode()
            return result
        }
    }
}

