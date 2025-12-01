package dev.mayaqq.cynosure.core.extensions

import dev.mayaqq.cynosure.Cynosure
import java.util.*

public abstract class ExtensionRegistry<T : Any, Ext : Extension<T>>(
    private val baseClass: Class<T>,
    private val baseExtension: Class<Ext>
) {

    // TODO: Probably switch to using AttachedRegistry here somehow
    // TODO: Add deferred registration
    private val data: MutableMap<T, MutableMap<Class<*>, Ext>> = Collections.synchronizedMap(IdentityHashMap())

    /**
     * Register an extension for an item, if you are making this item yourself, its better to simply
     * implement the interface on the item
     */
    @JvmOverloads
    public fun <E : Ext> register(value: T, extension: E, allowOverride: Boolean = false) {
        val extensions = data.getOrPut(value, ::HashMap)
        val clazz = extension.javaClass
        require(!baseClass.isAssignableFrom(clazz))
        val eClasses = clazz.findExtensionInterfaces()
        for (eClass in eClasses) {
            require(!eClass.isAssignableFrom(value.javaClass)) { "$value already implements extension $eClass" }
            require(allowOverride || !extensions.containsKey(eClass))
            extensions[eClass] = extension
        }
    }

    /**
     *
     */
    public fun <E : Ext> getExtension(clazz: Class<E>, value: T): E? {
        return if (clazz.isAssignableFrom(value.javaClass)) value as E
        else data[value]?.get(clazz) as? E
    }

    private fun Class<*>.findExtensionInterfaces(): List<Class<*>> {
        if (this.interfaces.contains(baseExtension)) return listOf(this)
        return this.interfaces.filter { it.interfaces.contains(baseExtension) }
            .onEach { Cynosure.debug("Found extension {} on {}", it, this) }
    }

}