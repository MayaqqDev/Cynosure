package dev.mayaqq.cynosure.client.keymapping

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.client.keymapping.KeyMappingRegistryImpl.mappings
import dev.mayaqq.cynosure.client.keymapping.KeyMappingRegistryImpl.registered
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import org.apache.commons.lang3.ArrayUtils

internal object KeyMappingRegistryImpl : KeyMappingRegistry {

    internal val mappings = mutableListOf<KeyMapping>()
    internal var registered = false

    /**
     * Register a key mapping
     * @param mapping the key mapping to register
     */
    override fun register(mapping: KeyMapping): KeyMapping {
        if (registered) {
            val options = Minecraft.getInstance().options
            options.keyMappings = ArrayUtils.add(options.keyMappings, mapping)
            Cynosure.info("Keymapping not registered properly: ${mapping.name}")
        } else {
            mappings.add(mapping)
        }
        return mapping
    }
}

public object KeyMappingRegistryEventSubscriber {
    @SubscribeEvent
    public fun onKeyMappingRegister(event: RegisterKeyMappingsEvent) {
        mappings.forEach(event::register)
        mappings.clear()
        registered = true
    }
}