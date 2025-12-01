package dev.mayaqq.cynosure.client.events.render

import dev.mayaqq.cynosure.client.render.gui.HudOverlayRegistry
import dev.mayaqq.cynosure.events.api.CancellableEvent
import dev.mayaqq.cynosure.events.api.Event
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics

/**
 * Invoked at the start of hud rendering. Cancelling cancels all hud rendering. For more precise
 * rendering of hud overlays, use [HudOverlayRegistry]
 */
public class BeginHudRenderEvent(
    public val gui: Gui,
    public val graphics: GuiGraphics,
    public val partialTicks: Float
) : CancellableEvent()

/**
 * Invoked after hud rendering. For more precise rendering of hud overlays, use [HudOverlayRegistry]
 */
public class EndHudRenderEvent(
    public val gui: Gui,
    public val graphics: GuiGraphics,
    public val partialTicks: Float
) : Event