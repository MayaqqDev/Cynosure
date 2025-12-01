package dev.mayaqq.cynosure.client.keymapping

import dev.mayaqq.cynosure.internal.loadPlatform
import net.minecraft.client.KeyMapping
import org.jetbrains.annotations.ApiStatus

@ApiStatus.NonExtendable
public interface KeyMappingRegistry {
    public companion object Impl : KeyMappingRegistry by loadPlatform()

    /**
     * Register a key mapping
     * @param mapping the key mapping to register
     */
    public fun register(mapping: KeyMapping) : KeyMapping
}

public fun KeyMapping.register(): KeyMapping = KeyMappingRegistry.register(this)
