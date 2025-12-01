package dev.mayaqq.cynosure.client.tooltips

import com.google.common.collect.ImmutableMap
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.transactions.transaction
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.minecraftforge.client.gui.ClientTooltipComponentManager
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import java.lang.reflect.Field
import java.util.function.Function

@EventBusSubscriber(value = [Dist.CLIENT], modid = MODID, bus = EventBusSubscriber.Bus.MOD)
@Suppress("UNCHECKED_CAST")
internal object ClientTooltipFactoriesImpl : ClientTooltipFactories {

    private val FACTORIES: Field = ClientTooltipComponentManager::class.java
        .getDeclaredField("FACTORIES")
        .apply { isAccessible = true }

    private val deferredComponents: MutableMap<Class<out TooltipComponent>, (TooltipComponent) -> ClientTooltipComponent> = mutableMapOf()
    private var eventFired: Boolean = false

    override fun <T : TooltipComponent> register(klass: Class<T>, factory: (T) -> ClientTooltipComponent) {
        if (eventFired) {
            val oldMap = FACTORIES[null]
                    as ImmutableMap<Class<out TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>>
            val newMap = ImmutableMap.copyOf(oldMap + (klass to Function(factory)))
            FACTORIES[null] = newMap
        } else {
            deferredComponents[klass] = factory as (TooltipComponent) -> ClientTooltipComponent
        }

        transaction {
            commit
        }

    }

    override fun create(component: TooltipComponent): ClientTooltipComponent? = ClientTooltipComponentManager.createClientTooltipComponent(component)

    @SubscribeEvent
    internal fun registerTooltipComponentsEvent(event: RegisterClientTooltipComponentFactoriesEvent) {
        for ((clazz, factory) in deferredComponents)
            event.register(clazz, factory)
        eventFired = true
    }
}