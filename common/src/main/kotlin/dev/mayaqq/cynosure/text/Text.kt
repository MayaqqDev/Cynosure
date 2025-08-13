package dev.mayaqq.cynosure.text

import dev.mayaqq.cynosure.helpers.McFont
import net.minecraft.network.chat.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringUtil
import dev.mayaqq.cynosure.text.Text.asComponent
import dev.mayaqq.cynosure.text.TextStyle.color
import dev.mayaqq.cynosure.utils.colors.Color
import dev.mayaqq.cynosure.utils.colors.McRed
import dev.mayaqq.cynosure.utils.colors.White
import dev.mayaqq.cynosure.utils.colors.minecraft.toColor
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import java.util.*

public object CommonText {

    public val NEWLINE: MutableComponent = "\n".asComponent()
    public val HYPHEN: MutableComponent = "-".asComponent()
    public val SPACE: MutableComponent = " ".asComponent()
    public val EMPTY: MutableComponent = "".asComponent()

    internal val PREFIX = Text.of("[Cynosure] ") { color = McRed }
}

public object Text {

    public fun of(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = text.asComponent(init)
    public fun of(init: MutableComponent.() -> Unit = {}): MutableComponent = "".asComponent(init)
    public fun translatable(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = Component.translatable(text).also(init)
    public fun String.asComponent(init: MutableComponent.() -> Unit = {}): MutableComponent = Component.literal(this).also(init)

    @JvmOverloads
    public fun multiline(vararg lines: Any?, init: MutableComponent.() -> Unit = {}): MutableComponent = join(*lines, separator = CommonText.NEWLINE, init = init)

    @JvmOverloads
    public fun join(vararg components: Any?, separator: MutableComponent? = null, init: MutableComponent.() -> Unit = {}): MutableComponent {
        val result = Component.literal("")
        components.forEachIndexed { index, it ->
            when (it) {
                is Component -> result.append(it)
                is String -> result.append(it)
                is List<*> -> result.append(join(*it.toTypedArray(), separator = separator))
                null -> return@forEachIndexed
                else -> error("Unsupported type: ${it::class.simpleName}")
            }

            if (index < components.size - 1 && separator != null) {
                result.append(separator)
            }
        }
        return result.also(init)
    }

    public fun MutableComponent.prefix(prefix: String): MutableComponent = join(prefix, this)
    public fun MutableComponent.suffix(suffix: String): MutableComponent = join(this, suffix)
    public fun MutableComponent.wrap(prefix: String, suffix: String) = this.prefix(prefix).suffix(suffix)

    public fun Component.sendToAll(level: Level) {
        level.server?.playerList?.players?.forEach { player ->
            player.displayClientMessage(this, false)
        }
    }

    public fun Component.sendTo(player: ServerPlayer) {
        player.displayClientMessage(this, false)
    }

    public fun MutableComponent.sendToAll(level: Level) {
        level.server?.playerList?.players?.forEach { player ->
            player.displayClientMessage(this, false)
        }
    }

    public fun MutableComponent.sendTo(player: ServerPlayer) {
        player.displayClientMessage(this, false)
    }
}

public object TextProperties {

    public val Component.width: Int get() = McFont.width(this)
    public val Component.stripped: String get() = StringUtil.stripColor(this.string)
}

public object TextUtils {

    public fun Component.splitLines(): List<Component> = split("\n")

    public fun Component.split(separator: String): List<Component> {
        val components = mutableListOf<Component>()
        var current = Component.empty()

        this.visit(
            { style, part ->
                val lines = part.split(separator)
                current.append(Component.literal(lines[0]).setStyle(style))
                if (lines.size > 1) {
                    components.add(current)
                    for (i in 1 until lines.lastIndex) {
                        components.add(Component.literal(lines[i]).setStyle(style))
                    }
                    current = Component.literal(lines.last()).setStyle(style)
                }
                Optional.empty<Unit>()
            },
            Style.EMPTY,
        )

        return components + current
    }

    private fun <T> split(
        splits: List<T>,
        maxWidth: Int,
        calc: (T) -> Int,
        joiner: (List<T>) -> T,
    ): List<T> {
        val output = mutableListOf<T>()
        var current = mutableListOf<T>()
        var currentLength = 0
        for (split in splits) {
            val splitWidth = calc.invoke(split)
            if (currentLength + splitWidth > maxWidth) {
                output.add(joiner.invoke(current))
                current.clear()
                currentLength = 0
            }
            current.add(split)
            currentLength += splitWidth
        }

        if (current.isNotEmpty()) {
            output.add(joiner.invoke(current))
        }

        return output
    }

    public fun Component.splitToWidth(separator: String, maxWidth: Int): List<Component> = split(
        this.split(separator),
        maxWidth,
        McFont::width,
    ) { Text.join(*it.toTypedArray(), Text.of(separator)) }

    public fun String.splitToWidth(separator: String, maxWidth: Int): List<String> = split(
        this.split(separator),
        maxWidth,
        McFont::width,
    ) { it.joinToString(separator) }

}

public object TextStyle {

    public fun MutableComponent.style(init: Style.() -> Style): MutableComponent {
        this.withStyle { init.invoke(style) }
        return this
    }

    public var MutableComponent.font: ResourceLocation?
        get() = this.style.font
        set(value) {
            this.style { withFont(value) }
        }

    public var MutableComponent.hover: Component?
        get() = this.style.hoverEvent?.takeIf { it.action == HoverEvent.Action.SHOW_TEXT }?.getValue(HoverEvent.Action.SHOW_TEXT)
        set(value) {
            this.style { withHoverEvent(value?.let { HoverEvent(HoverEvent.Action.SHOW_TEXT, it) }) }
        }

    public var MutableComponent.command: String?
        get() = this.style.clickEvent?.takeIf { it.action == ClickEvent.Action.RUN_COMMAND }?.value
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent(ClickEvent.Action.RUN_COMMAND, it) }) }
        }

    public var MutableComponent.suggest: String?
        get() = this.style.clickEvent?.takeIf { it.action == ClickEvent.Action.SUGGEST_COMMAND }?.value
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, it) }) }
        }

    public var MutableComponent.url: String?
        get() = this.style.clickEvent?.takeIf { it.action == ClickEvent.Action.OPEN_URL }?.value
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent(ClickEvent.Action.OPEN_URL, it) }) }
        }

    public var MutableComponent.color: Color
        get() = this.style.color?.toColor() ?: White
        set(value) {
            this.style { withColor(value.toInt()) }
        }

    public var MutableComponent.bold: Boolean
        get() = this.style.isBold
        set(value) {
            this.style { withBold(value) }
        }

    public var MutableComponent.italic: Boolean
        get() = this.style.isItalic
        set(value) {
            this.style { withItalic(value) }
        }

    public var MutableComponent.underlined: Boolean
        get() = this.style.isUnderlined
        set(value) {
            this.style { withUnderlined(value) }
        }

    public var MutableComponent.strikethrough: Boolean
        get() = this.style.isStrikethrough
        set(value) {
            this.style { withStrikethrough(value) }
        }

    public var MutableComponent.obfuscated: Boolean
        get() = this.style.isObfuscated
        set(value) {
            this.style { withObfuscated(value) }
        }
}

public object TextBuilder {
    public fun MutableComponent.append(component: Component, init: MutableComponent.() -> Unit) = this.append(component.copy().apply(init))
    public fun MutableComponent.append(text: String, init: MutableComponent.() -> Unit = {}) = this.append(text.asComponent(init))
    public fun MutableComponent.append(number: Number, init: MutableComponent.() -> Unit = {}) = this.append(number.toString().asComponent(init))
    public fun MutableComponent.append(boolean: Boolean, init: MutableComponent.() -> Unit = {}) = this.append(boolean.toString().asComponent(init))
}