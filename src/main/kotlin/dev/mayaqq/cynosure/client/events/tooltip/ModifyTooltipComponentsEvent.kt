package dev.mayaqq.cynosure.client.events.tooltip

import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.items.extensions.CustomTooltip
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

/**
 * Generic event for modifying tooltip components. For specific stuff use the item extension [CustomTooltip].
 * This event wraps the component list so you can call list methods like [add] on it directly
 *
 * @property stack the [ItemStack] this tooltip is on
 * @property flag provided tooltip flags
 * @property player the player holding the item
 */
public class ModifyTooltipComponentsEvent(
    public val stack: ItemStack,
    public val flag: TooltipFlag,
    public val player: Player?,
    components: MutableList<Component>
) : Event, MutableList<Component> by components