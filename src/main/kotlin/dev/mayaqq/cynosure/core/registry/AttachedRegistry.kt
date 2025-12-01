package dev.mayaqq.cynosure.core.registry

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.CynosureInternal
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation

public class AttachedRegistry<K : Any, T : Any>(
    objectRegistry: () -> Registry<K>
) : NamedRegistry<T>(/* TODO: Default based on registry */)  {

    private val objectRegistry by lazy(objectRegistry)
    private val objectMap: MutableMap<K, T> = mutableMapOf()
    private val deferredRegistrations: MutableMap<ResourceLocation, (K) -> T> = mutableMapOf()
    private var unwrapped = false

    init {
        ALL.add(this)
    }

    override fun register(key: ResourceLocation, value: T): T {
        super.register(key, value)
        if (unwrapped) {
            val attached = objectRegistry.get(key) ?: error("No attachement object found for ket $key")
            objectMap[attached] = value
        }
        return value
    }

    public fun register(attached: K, value: T): T {
        check(unwrapped) { """
            Cannot register directly against key before unwrap.
            Use register(ResourceLocation, T) or regsterDeferred(ResourceLocation, (K) -> T) instead
        """.trimIndent() }

        val attachedKey = objectRegistry.getKey(attached) ?: error("No attachement key found for object $attached")
        super.register(attachedKey, value)
        objectMap[attached] = value
        return value
    }

    public fun registerDeferred(id: ResourceLocation, factory: (K) -> T) {
        if (unwrapped) {
            val attached = objectRegistry[id] ?: error("No attachment found for key $id")
            val value = factory(attached)
            super.register(id, value)
            objectMap[attached] = value
        } else {
            check(!deferredRegistrations.containsKey(id)) { "Key already present $id" }
            deferredRegistrations[id] = factory
        }
    }


    private fun unwrap() {
        for ((id, item) in idMap) {
            val k = objectRegistry[id]
            if (k == null) {
                Cynosure.error("Could not get object key for id $id")
                idMap.remove(id)
                continue
            }

            objectMap[k] = item
        }

        for ((id, factory) in deferredRegistrations) {
            val k = objectRegistry[id]
            if (k == null) {
                Cynosure.error("Could not get object key for deferred registration $id")
                continue
            }

            val value = factory(k)
            idMap[id] = value
            objectMap[k] = value
        }

        deferredRegistrations.clear()
    }

    internal companion object {
        private val ALL: MutableList<AttachedRegistry<*, *>> = mutableListOf()

        @CynosureInternal
        fun unwrapAll() {
            ALL.forEach(AttachedRegistry<*, *>::unwrap)
        }
    }
}