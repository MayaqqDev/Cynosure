@file:EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
package dev.mayaqq.cynosure.client.keymapping

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.client.keymapping.KeyMappingRegistryImpl.mappings
import dev.mayaqq.cynosure.client.keymapping.KeyMappingRegistryImpl.registered
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
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

@SubscribeEvent
public fun onKeyMappingRegister(event: RegisterKeyMappingsEvent) {
    mappings.forEach(event::register)
    mappings.clear()
    registered = true
}