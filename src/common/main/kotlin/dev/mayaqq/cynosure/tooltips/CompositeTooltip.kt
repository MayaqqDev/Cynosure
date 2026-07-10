package dev.mayaqq.cynosure.tooltips

import dev.mayaqq.cynosure.items.extensions.CustomTooltip
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

public class CompositeTooltip(public val tooltips: List<CustomTooltip>) : CustomTooltip {
    override fun MutableList<Component>.modifyTooltip(stack: ItemStack, player: Player?, flags: TooltipFlag) {
        tooltips.forEach { with(it) { modifyTooltip(stack, player, flags) } }
    }

    public constructor(vararg tooltips: CustomTooltip): this(tooltips.toList())
}