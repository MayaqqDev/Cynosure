package dev.mayaqq.cynosure.client.tooltips

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.world.inventory.tooltip.TooltipComponent
import java.util.concurrent.ConcurrentHashMap

private typealias TooltipComponentFactory<T> = (T) -> ClientTooltipComponent

internal class ClientTooltipFactoriesImpl : ClientTooltipFactories, TooltipComponentCallback {

    private val factories: MutableMap<Class<out TooltipComponent>, TooltipComponentFactory<TooltipComponent>> = ConcurrentHashMap()

    init {
        TooltipComponentCallback.EVENT.register(this)
    }

    override fun <T : TooltipComponent> register(klass: Class<T>, factory: (T) -> ClientTooltipComponent) {
        require(!factories.containsKey(klass)) { "Tooltip factory for $klass already registered" }
        factories[klass] = factory as TooltipComponentFactory<TooltipComponent>
    }

    override fun create(component: TooltipComponent): ClientTooltipComponent? {
        return factories[component.javaClass]?.invoke(component)
    }

    override fun getComponent(p0: TooltipComponent?): ClientTooltipComponent? = p0?.let(::create)
}