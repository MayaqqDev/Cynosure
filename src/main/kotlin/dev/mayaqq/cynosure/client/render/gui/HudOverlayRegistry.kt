package dev.mayaqq.cynosure.client.render.gui

import dev.mayaqq.cynosure.CynosureInternal
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import java.util.*

public object HudOverlayRegistry {

    private val overlays: MutableMap<ResourceLocation, Entry> = HashMap()

    @CynosureInternal
    public val sorted: Map<VanillaHud, List<HudOverlay>> by lazy {
        val map: MutableMap<VanillaHud, MutableList<Entry>> = EnumMap(VanillaHud::class.java)
        for((_, entry) in overlays) {
            map.getOrPut(entry.anchor, ::ArrayList).add(entry)
        }

        val output: MutableMap<VanillaHud, List<HudOverlay>> = EnumMap(VanillaHud::class.java)
        for(key in VanillaHud.entries) {
            output[key] = map[key]?.sorted()?.map { it.overlay } ?: emptyList()
        }

        frozen = true
        return@lazy output
    }

    private var frozen: Boolean = false

    public fun register(anchor: VanillaHud, id: ResourceLocation, overlay: HudOverlay, ordering: Int = 0) {
        require(!frozen) { "Too late to register hud overlay" }
        require(!overlays.containsKey(id)) { "Duplicate id: $id" }
        overlays[id] = Entry(overlay, anchor, ordering)
    }

    public operator fun get(id: ResourceLocation): HudOverlay? = overlays[id]?.overlay

    private data class Entry(val overlay: HudOverlay, val anchor: VanillaHud, val ordering: Int) : Comparable<Entry> {
        override fun compareTo(other: Entry): Int = ordering.compareTo(other.ordering)
    }
}

public fun interface HudOverlay {
    public fun render(gui: Gui, graphics: GuiGraphics, partialTick: Float)
}

public enum class VanillaHud(forgeid: String) {
    VIGNETTE("vignette"),
    SPYGLASS("spyglass"),
    HELMET("helmet"),
    FROSTBITE("frostbite"),
    PORTAL("portal"),
    HOTBAR("hotbar"),
    CROSSHAIR("crosshair"),
    BOSS_BAR("boss_event_progress"),
    PLAYER_HEALTH("player_health"),
    MOUNT_HEALTH("mount_health"),
    ARMOR_LEVEL("armor_level"),
    FOOD_LEVEL("food_level"),
    AIR_LEVEL("air_level"),
    JUMP_BAR("jump_bar"),
    XP_BAR("experience_bar"),
    ITEM_NAME("item_name"),
    SLEEP_FADE("sleep_fade"),
    EFFECTS("potion_icons"),
    DEBUG("debug_text"),
    OVERLAY_MESSAGE("record_overlay"),
    TITLE_TEXT("title_text"),
    SUBTITLES("subtitles"),
    SCOREBOARD("scoreboard"),
    CHAT("chat_panel"),
    PLAYER_LIST("player_list");

    public val forgeId: ResourceLocation = ResourceLocation("minecraft", forgeid)
}