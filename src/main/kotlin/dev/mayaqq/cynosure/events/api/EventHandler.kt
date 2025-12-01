package dev.mayaqq.cynosure.events.api

import dev.mayaqq.cynosure.DEBUG_DIR
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import java.io.File
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.function.Consumer

private const val SPLIT_POINT = 200

internal fun List<EventListener>.createHandler(event: Class<out Event>): Consumer<Any> {
    var consumer: Consumer<Any>? = null
    var i = size;
    while (i >= SPLIT_POINT) {
        consumer = subList(i - SPLIT_POINT, i).createPartHandler(event, consumer)
        i -= SPLIT_POINT
    }
    return if (i > 0) subList(0, i).createPartHandler(event, consumer) else consumer ?: Consumer {}
}

private fun List<EventListener>.createPartHandler(event: Class<out Event>, nextHandler: Consumer<Any>?): Consumer<Any> {
    val cw = ClassWriter(0)
    val eventClass = event.name.replace('.', '/')

    val thisClass = "dev/mayaqq/cynosure/events/api/EventHandler$${eventClass.replace('/', '$')}"
    cw.visit(
        V17, ACC_PUBLIC or ACC_FINAL, thisClass,
        null, "java/lang/Object", arrayOf("java/util/function/Consumer")
    )

    val instances = map(EventListener::invokerType)
        .filterIsInstance<InvokerType.VirtualWithInstance>()
        .toCollection(ObjectLinkedOpenHashSet())

    val init = cw.visitMethod(
        ACC_PRIVATE, "<init>",
        "(${instances.joinToString("") { Type.getDescriptor(it.clazz) } + (nextHandler?.let { "Ljava/util/function/Consumer;" } ?: "")})V",
        null, null
    )
    init.visitCode()
    init.visitVarInsn(ALOAD, 0)
    init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)

    instances.forEachIndexed { index, instance ->
        val desc = Type.getDescriptor(instance.clazz)
        cw.visitField(ACC_PRIVATE or ACC_FINAL, "instance$$index", desc, null, null).visitEnd()
        init.visitVarInsn(ALOAD, 0)
        init.visitVarInsn(ALOAD, index + 1)
        init.visitFieldInsn(PUTFIELD, thisClass, "instance$$index", desc)
    }

    if (nextHandler != null) {
        cw.visitField(ACC_PRIVATE or ACC_FINAL, "next", "Ljava/util/function/Consumer;", null, null)
        init.visitVarInsn(ALOAD, 0)
        init.visitVarInsn(ALOAD, instances.size + 1)
        init.visitFieldInsn(PUTFIELD, thisClass, "next", "Ljava/util/function/Consumer;")
    }

    init.visitInsn(RETURN)
    init.visitMaxs(2, instances.size + if (nextHandler != null) 2 else 1)
    init.visitEnd()


    val accept = cw.visitMethod(ACC_PUBLIC, "accept", "(Ljava/lang/Object;)V", null, null)
    accept.visitCode()
    accept.visitVarInsn(ALOAD, 1)
    accept.visitTypeInsn(CHECKCAST, eventClass)
    accept.visitVarInsn(ASTORE, 1)

    var maxMarker = false
    var nextLabel: Label? = null

    forEach { listener ->
        nextLabel?.let {
            accept.visitLabel(it)
            accept.visitFrame(F_FULL, 2, arrayOf<Any>(thisClass, eventClass), 0, emptyArray<Any>())
        }

        val lbl = Label()
        if (!listener.receiveCancelled) {
            accept.visitVarInsn(ALOAD, 1)
            accept.visitMethodInsn(INVOKEVIRTUAL, eventClass, "isCancelled", "()Z", false)
            accept.visitFrame(F_FULL, 2, arrayOf<Any>(thisClass, eventClass), 1, arrayOf<Any>(INTEGER))
            accept.visitJumpInsn(IFNE, lbl)
        }
        nextLabel = lbl

        when (val invoker = listener.invokerType) {
            InvokerType.Static -> {
                accept.visitVarInsn(ALOAD, 1)
                if (listener.event != event) accept.visitTypeInsn(CHECKCAST, listener.event.name.replace('.', '/'))
                accept.visitMethodInsn(INVOKESTATIC, listener.className, listener.methodName, listener.methodDesc, false)
            }
            is InvokerType.VirtualWithInstance -> {
                accept.visitVarInsn(ALOAD, 0)
                accept.visitFieldInsn(GETFIELD, thisClass, "instance$${instances.indexOf(invoker)}", "L${listener.className};")
                accept.visitVarInsn(ALOAD, 1)
                if (listener.event != event) accept.visitTypeInsn(CHECKCAST, listener.event.name.replace('.', '/'))
                accept.visitMethodInsn(invoker.opcode, listener.className, listener.methodName, listener.methodDesc, invoker.clazz.isInterface)
                maxMarker = true
            }
            is InvokerType.VirtualWithOwner -> {
                accept.visitFieldInsn(GETSTATIC, invoker.ownerFieldName, invoker.ownerClassName, "L${listener.className};")
                accept.visitVarInsn(ALOAD, 1)
                if (listener.event != event) accept.visitTypeInsn(CHECKCAST, listener.event.name.replace('.', '/'))
                accept.visitMethodInsn(invoker.opcode, listener.className, listener.methodName, listener.methodDesc, false)
                maxMarker = true
            }
        }

        if (!listener.methodDesc.endsWith("V")) accept.visitInsn(POP)
    }

    nextLabel?.let(accept::visitLabel)
    accept.visitFrame(F_FULL, 2, arrayOf<Any>(thisClass, eventClass), 0, emptyArray<Any>())
    if (nextHandler != null) {
        accept.visitVarInsn(ALOAD, 0)
        accept.visitFieldInsn(GETFIELD, thisClass, "next", "Ljava/util/function/Consumer;")
        accept.visitVarInsn(ALOAD, 1)
        accept.visitMethodInsn(INVOKEINTERFACE, "java/util/function/Consumer", "accept", "(Ljava/lang/Object;)V", true)
    }
    accept.visitInsn(RETURN)
    accept.visitMaxs(if (maxMarker || nextHandler != null) 3 else 2, 2)
    accept.visitEnd()
    cw.visitEnd()

    val bytes = cw.toByteArray()
    if (System.getProperty("cynosure.dumpEventHandlers").toBoolean()) {
        val dump = DEBUG_DIR.resolve("dump-${bytes.hashCode()}")
        DEBUG_DIR.mkdirs()
        dump.writeBytes(bytes)
    }
    val lookup = MethodHandles.lookup().defineHiddenClass(cw.toByteArray(), true)
    val ctor = lookup.findConstructor(
        lookup.lookupClass(),
        MethodType.methodType(Nothing::class.javaPrimitiveType, instances.map(InvokerType.VirtualWithInstance::clazz).let { if (nextHandler != null) it + Consumer::class.java else it })
    )

    return ctor.invokeWithArguments(
        instances.map(InvokerType.VirtualWithInstance::instance)
            .let { if (nextHandler != null) it + nextHandler else it }
    ) as Consumer<Any>
}