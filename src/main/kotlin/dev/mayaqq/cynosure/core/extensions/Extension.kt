package dev.mayaqq.cynosure.core.extensions

public interface Extension<T : Any>

public inline fun <T : Any, reified E : Extension<T>> ExtensionRegistry<T, in E>.getExtension(value: T): E? =
    getExtension(E::class.java, value)