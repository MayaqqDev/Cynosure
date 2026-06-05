package dev.mayaqq.cynosure.client.render.gui

import dev.mayaqq.cynosure.CynosureInternal
import invoke.kitty.kritter.platform.identifierOf
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import java.util.*

public object HudOverlayRegistry {

    private val overlays: MutableMap<ResourceLocation, Entry> = HashMap()

    @CynosureInternal
    public val sorted: Map<ResourceLocation, List<HudOverlay>> by lazy {
        val map: MutableMap<ResourceLocation, MutableList<Entry>> = mutableMapOf()
        for((_, entry) in overlays) {
            map.getOrPut(entry.vanillaAnchor(), ::ArrayList).add(entry)
        }

        val output: MutableMap<ResourceLocation, List<HudOverlay>> = mutableMapOf()
        for(key in VanillaHud.ids) {
            output[key] = map[key]?.sorted()?.map { it.overlay } ?: emptyList()
        }

        frozen = true
        return@lazy output
    }

    private var frozen: Boolean = false

    public fun register(anchor: ResourceLocation, id: ResourceLocation, overlay: HudOverlay, ordering: Int = 0) {
        require(!frozen) { "Too late to register hud overlay" }
        require(!overlays.containsKey(id)) { "Duplicate id: $id" }
        overlays[id] = Entry(overlay, anchor, ordering)
    }

    public operator fun get(id: ResourceLocation): HudOverlay? = overlays[id]?.overlay

    private data class Entry(val overlay: HudOverlay, val anchor: ResourceLocation, val ordering: Int) : Comparable<Entry> {
        fun vanillaAnchor(): ResourceLocation {
            var anchor = this.anchor
            while (anchor !in VanillaHud.ids)
                anchor = requireNotNull(overlays[anchor]).anchor
            return anchor
        }
        override fun compareTo(other: Entry): Int = ordering.compareTo(other.ordering)
    }
}

public fun interface HudOverlay {
    public fun render(gui: Gui, graphics: GuiGraphics, partialTick: Float)
}

public object VanillaHud {
    @JvmField public val VIGNETTE: ResourceLocation = create("vignette")
    @JvmField public val SPYGLASS: ResourceLocation = create("spyglass")
    @JvmField public val HELMET: ResourceLocation = create("helmet")
    @JvmField public val FROSTBITE: ResourceLocation = create("frostbite")
    @JvmField public val PORTAL: ResourceLocation = create("portal")
    @JvmField public val HOTBAR: ResourceLocation = create("hotbar")
    @JvmField public val CROSSHAIR: ResourceLocation = create("crosshair")
    @JvmField public val BOSS_BAR: ResourceLocation = create("boss_event_progress")
    @JvmField public val PLAYER_HEALTH: ResourceLocation = create("player_health")
    @JvmField public val MOUNT_HEALTH: ResourceLocation = create("mount_health")
    @JvmField public val ARMOR_LEVEL: ResourceLocation = create("armor_level")
    @JvmField public val FOOD_LEVEL: ResourceLocation = create("food_level")
    @JvmField public val AIR_LEVEL: ResourceLocation = create("air_level")
    @JvmField public val JUMP_BAR: ResourceLocation = create("jump_bar")
    @JvmField public val XP_BAR: ResourceLocation = create("experience_bar")
    @JvmField public val ITEM_NAME: ResourceLocation = create("item_name")
    @JvmField public val SLEEP_FADE: ResourceLocation = create("sleep_fade")
    @JvmField public val EFFECTS: ResourceLocation = create("potion_icons")
    @JvmField public val DEBUG: ResourceLocation = create("debug_text")
    @JvmField public val OVERLAY_MESSAGE: ResourceLocation = create("record_overlay")
    @JvmField public val TITLE_TEXT: ResourceLocation = create("title_text")
    @JvmField public val SUBTITLES: ResourceLocation = create("subtitles")
    @JvmField public val SCOREBOARD: ResourceLocation = create("scoreboard")
    @JvmField public val CHAT: ResourceLocation = create("chat_panel")
    @JvmField public val PLAYER_LIST: ResourceLocation = create("player_list")

    public val ids: LinkedHashSet<ResourceLocation> = linkedSetOf()


    private fun create(id: String) = identifierOf("minecraft", id).also { ids.add(it) }
}