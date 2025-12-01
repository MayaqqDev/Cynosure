package dev.mayaqq.cynosure.tooltips

import dev.mayaqq.cynosure.helpers.McFont
import dev.mayaqq.cynosure.injection.client.javaLocale
import dev.mayaqq.cynosure.items.extensions.CustomTooltip
import dev.mayaqq.cynosure.utils.Couple
import dev.mayaqq.cynosure.utils.colors.Color
import dev.mayaqq.cynosure.utils.colors.DarkGray
import dev.mayaqq.cynosure.utils.colors.LightGray
import dev.mayaqq.cynosure.utils.get
import dev.mayaqq.cynosure.utils.language.words
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
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

    private val pressShiftComponentOff = Component.translatable("tooltip.generic.cynosure.hold_shift.off")
    private val pressShiftComponentOn = Component.translatable("tooltip.generic.cynosure.hold_shift.on")

    override fun MutableList<Component>.modifyTooltip(stack: ItemStack, player: Player?, flags: TooltipFlag) {
        if (checkLanguage()) {
            rebuild(stack.item)
        }
        if (Screen.hasShiftDown()) {
            addAll(1, lines)
            add(1, pressShiftComponentOn)
        } else {
            add(1, pressShiftComponentOff)
        }
    }

    private fun rebuild(stack: Item) {
        lines.clear()
        val key = stack.descriptionId + ".tooltip"
        // TODO: Split and all kinds of other stuff
        lines.add(Component.empty())
        lines.addAll(Component.translatable(key).format(theme))
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
        val highlightColor: Color,
        val indent: Int
    ) {
        public companion object {
            public val Default: Theme = Theme(
                DarkGray,
                LightGray,
                Color(0x84597Eu),
                0
            )
        }

        public fun with(
            primaryColor: Color = this.primaryColor,
            secondaryColor: Color = this.secodaryColor,
            highlightColor: Color = this.highlightColor,
            indent: Int = this.indent
            ): Theme = Theme(primaryColor, secondaryColor, highlightColor, indent)
    }

    private fun Component.format(theme: Theme): List<Component> {
        val lines = buildList {
            var totalWidth = 0
            var currentLine = ""
            string.words(Minecraft.getInstance().javaLocale).forEach { word ->
                val width = McFont.width(word.replace("_", ""))
                if (totalWidth + width > MAX_LINE_WIDTH) {
                    if (totalWidth > 0) {
                        add(currentLine)
                        currentLine = ""
                        totalWidth = 0
                    } else {
                        add(word)
                        return@forEach
                    }
                }
                totalWidth += width
                currentLine += word
            }
            if (totalWidth > 0) add(currentLine)
        }


        // Format
        val lineStart = Component.literal(" ".repeat(theme.indent))
        lineStart.withStyle{it.withColor(theme.primaryColor.toInt())}
        val formattedLines = mutableListOf<Component>()
        val styles: Couple<Color> = theme.primaryColor to theme.highlightColor

        var currentlyHighlighted = false
        for (string in lines) {
            val currentComponent = lineStart.plainCopy()
            val split = string.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (part in split) {
                currentComponent.append(Component.literal(part).withStyle{it.withColor(styles[currentlyHighlighted].toInt())})
                currentlyHighlighted = !currentlyHighlighted
            }

            formattedLines.add(currentComponent)
            currentlyHighlighted = !currentlyHighlighted
        }

        return formattedLines
    }

    public companion object {
        public const val MAX_LINE_WIDTH: Int = 200
    }
}