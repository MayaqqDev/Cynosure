    @file:Suppress("Deprecation")
package dev.mayaqq.cynosure.utils.unsafe

import sun.misc.Unsafe
import java.lang.reflect.Field

@RequiresOptIn(level = RequiresOptIn.Level.WARNING, message = "This API makes use of sun.misc.Unsafe, use at your own risk")
public annotation class CynosureUnsafeApi

@CynosureUnsafeApi
internal val UNSAFE: Unsafe by lazy {
    try {
        val theUnsafe: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
        theUnsafe.setAccessible(true)
        theUnsafe.get(null) as Unsafe
    } catch (e: Exception) {
        throw RuntimeException("Unable to capture unsafe", e)
    }
}

@CynosureUnsafeApi
public fun setFieldUnsafe(instance: Any, fieldName: String, value: Any?) {
    try {
        val field: Field = instance.javaClass.getDeclaredField(fieldName)
        field.setAccessible(true)
        setFieldUnsafe(instance, field, value)
    } catch (e: Exception) {
        throw RuntimeException("Unable to set field", e)
    }
}

@CynosureUnsafeApi
public fun setFieldUnsafe(instance: Any, field: Field, value: Any?) {
    try {
        UNSAFE.putObject(instance, UNSAFE.objectFieldOffset(field), value)
    } catch (e: Exception) {
        throw RuntimeException("Unable to set field", e)
    }
}

@CynosureUnsafeApi
public fun getFieldUnsafe(instance: Any, fieldName: String): Any? {
    try {
        val field: Field = instance.javaClass.getDeclaredField(fieldName)
        field.setAccessible(true)
        return getFieldUnsafe(instance, field)
    } catch (e: Exception) {
        throw RuntimeException("Unable to set field", e)
    }
}

@CynosureUnsafeApi
public fun getFieldUnsafe(instance: Any, field: Field): Any? {
    try {
        val offset = UNSAFE.objectFieldOffset(field)
        return UNSAFE.getObject(instance, offset)
    } catch (e: Exception) {
        throw RuntimeException("Unable to get field", e)
    }
}

@CynosureUnsafeApi
public fun getStaticFieldUnsafe(clazz: Class<*>, fieldName: String): Any? {
    try {
        val field: Field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return getStaticFieldUnsafe(field)
    } catch (e: Exception) {
        throw RuntimeException("Unable to get static field", e)
    }
}

@CynosureUnsafeApi
public fun getStaticFieldUnsafe(field: Field): Any? {
    try {
        val offset: Long = UNSAFE.staticFieldOffset(field)
        val base: Any = UNSAFE.staticFieldBase(field)
        return UNSAFE.getObject(base, offset)
    } catch (e: Exception) {
        throw RuntimeException("Unable to get static field", e)
    }
}

@CynosureUnsafeApi
public fun setStaticFieldUnsafe(clazz: Class<*>, fieldName: String, value: Any?) {
    try {
        val field: Field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        setStaticFieldUnsafe(field, value)
    } catch (e: Exception) {
        throw RuntimeException("Unable to set static field", e)
    }
}

@CynosureUnsafeApi
public fun setStaticFieldUnsafe(field: Field, value: Any?) {
    try {
        val offset: Long = UNSAFE.staticFieldOffset(field)
        val base: Any = UNSAFE.staticFieldBase(field)
        UNSAFE.putObject(base, offset, value)
    } catch (e: Exception) {
        throw RuntimeException("Unable to set static field", e)
    }
}

internal inline fun <reified T : Any> getField(noinline predicate: (Field) -> Boolean): Field {
    return T::class.java.getDeclaredField(predicate)
}

internal fun Class<*>.getDeclaredField(predicate: (Field) -> Boolean): Field {
    try {
        for (field in declaredFields) {
            if (predicate(field)) {
                return field
            }
        }
    } catch (e: Exception) {
        throw RuntimeException("Unable to get field", e)
    }
    throw RuntimeException("Unable to find field for $name")
}