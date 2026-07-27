package dev.mayaqq.cynosure.client.render

import dev.mayaqq.cynosure.client.render.gui.VanillaHud
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.gui.VanillaGuiLayers.*

public val vanillaGuiLayers: List<ResourceLocation> = listOf(
    CAMERA_OVERLAYS,
    CROSSHAIR,
    HOTBAR,
    JUMP_METER,
    EXPERIENCE_BAR,
    PLAYER_HEALTH,
    ARMOR_LEVEL,
    FOOD_LEVEL,
    VEHICLE_HEALTH,
    AIR_LEVEL,
    SELECTED_ITEM_NAME,
    SPECTATOR_TOOLTIP,
    EXPERIENCE_LEVEL,
    EFFECTS,
    BOSS_OVERLAY,
    SLEEP_OVERLAY,
    DEMO_OVERLAY,
    DEBUG_OVERLAY,
    SCOREBOARD_SIDEBAR,
    OVERLAY_MESSAGE,
    TITLE,
    CHAT,
    TAB_LIST,
    SUBTITLE_OVERLAY,
    SAVING_INDICATOR,
)

public val oldToNew = mapOf<ResourceLocation, ResourceLocation>(
    VanillaHud.VIGNETTE to CAMERA_OVERLAYS,
    VanillaHud.SPYGLASS to CAMERA_OVERLAYS,
    VanillaHud.FROSTBITE to CAMERA_OVERLAYS,
    VanillaHud.HELMET to CAMERA_OVERLAYS,
    VanillaHud.PORTAL to CAMERA_OVERLAYS,
    VanillaHud.CROSSHAIR to CROSSHAIR,
    VanillaHud.HOTBAR to HOTBAR,
    VanillaHud.BOSS_BAR to BOSS_OVERLAY,
    VanillaHud.PLAYER_HEALTH to PLAYER_HEALTH,
    VanillaHud.MOUNT_HEALTH to VEHICLE_HEALTH,
    VanillaHud.ARMOR_LEVEL to ARMOR_LEVEL,
    VanillaHud.FOOD_LEVEL to FOOD_LEVEL,
    VanillaHud.AIR_LEVEL to AIR_LEVEL,
    VanillaHud.JUMP_BAR to JUMP_METER,
    VanillaHud.XP_BAR to EXPERIENCE_BAR,
    VanillaHud.ITEM_NAME to SELECTED_ITEM_NAME,
    VanillaHud.SLEEP_FADE to SLEEP_OVERLAY,
    VanillaHud.EFFECTS to EFFECTS,
    VanillaHud.DEBUG to DEBUG_OVERLAY,
    VanillaHud.OVERLAY_MESSAGE to OVERLAY_MESSAGE,
    VanillaHud.TITLE_TEXT to TITLE,
    VanillaHud.SUBTITLES to SUBTITLE_OVERLAY,
    VanillaHud.SCOREBOARD to SCOREBOARD_SIDEBAR,
    VanillaHud.CHAT to CHAT,
    VanillaHud.PLAYER_LIST to TAB_LIST
)