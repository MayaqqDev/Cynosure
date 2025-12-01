package dev.mayaqq.cynosure.client.tooltips

import dev.mayaqq.cynosure.internal.loadPlatform
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.world.inventory.tooltip.TooltipComponent

public interface ClientTooltipFactories {

    public fun <T : TooltipComponent> register(klass: Class<T>, factory: (T) -> ClientTooltipComponent)

    public fun create(component: TooltipComponent): ClientTooltipComponent?

    public companion object Impl : ClientTooltipFactories by loadPlatform() {
        public inline fun <reified T : TooltipComponent> register(noinline factory: (T) -> ClientTooltipComponent) {
            register(T::class.java, factory)
        }

    }
}