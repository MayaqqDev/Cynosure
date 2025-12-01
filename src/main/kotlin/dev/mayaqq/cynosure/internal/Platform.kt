package dev.mayaqq.cynosure.internal

import kotlin.reflect.full.companionObject

internal inline fun <reified T : Any> loadPlatform(implName: String = ""): T = loadPlatform(T::class.java, implName)

@JvmOverloads
internal fun <T : Any> loadPlatform(clazz: Class<T>, implName: String = ""): T {
    fun companionName() = clazz.kotlin.companionObject?.simpleName ?: "Impl"
    val implClass = Class.forName(implName.ifEmpty { "${clazz.canonicalName}${companionName()}" })
    if (!clazz.isAssignableFrom(implClass)) error("Impl class does not extend api class")
    return (implClass.kotlin.objectInstance ?: implClass.getConstructor().newInstance()) as T
}

