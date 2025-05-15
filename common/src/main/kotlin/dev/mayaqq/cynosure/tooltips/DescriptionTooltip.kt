package dev.mayaqq.cynosure.tooltips

import dev.mayaqq.cynosure.items.extensions.CustomTooltip
import dev.mayaqq.cynosure.utils.colors.Color
import dev.mayaqq.cynosure.utils.colors.DarkGray
import dev.mayaqq.cynosure.utils.colors.LightGray
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

public class DescriptionTooltip(
    private val theme: Theme
) : CustomTooltip {

    private val lines: MutableList<Component> = mutableListOf()
    private val hiddenLines: MutableList<Component> = mutableListOf()
    private var cachedLanguage: String? = null

    override fun MutableList<Component>.modifyTooltip(stack: ItemStack, player: Player, flags: TooltipFlag) {
        if (checkLanguage()) {
            rebuild(stack.item)
        }
    }

    private fun rebuild(stack: Item) {
        val key = stack.descriptionId + ".tooltip"
        // TODO: Split and all kinds of other stuff
        lines.add(Component.translatable(key).withStyle { it.withColor(theme.primaryColor.toInt()) })
    }

    private fun checkLanguage(): Boolean {
        val selected = Minecraft.getInstance().languageManager.selected
        if (selected != cachedLanguage) {
            cachedLanguage = selected
            return true
        }
        return false
    }

    public data class Theme(
        val primaryColor: Color,
        val secodaryColor: Color,
        val highlightColor: Color
    )

    public object Themes {

        @JvmField
        public val Default: Theme = Theme(DarkGray, LightGray, Color(0xAA00AAu))


    }
}