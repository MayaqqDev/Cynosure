package dev.mayaqq.cynosure.client.keymapping

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping

internal object KeyMappingRegistryImpl : KeyMappingRegistry {
    /**
     * Register a key mapping
     * @param mapping the key mapping to register
     */
    override fun register(mapping: KeyMapping): KeyMapping {
        return KeyBindingHelper.registerKeyBinding(mapping)
    }
}