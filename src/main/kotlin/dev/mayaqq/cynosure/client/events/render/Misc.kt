package dev.mayaqq.cynosure.client.events.render

import dev.mayaqq.cynosure.events.api.Event

/**
 * Invoked when the level renderer reloads
 */
public object ReloadLevelRendererEvent : Event

/**
 * Invoked when the game renderer resizes
 */
public class ResizeRendererEvent(public val width: Int, public val height: Int) : Event

/**
 * Invoked when the game renderer renders (each frame at the start)
 */
public class GameRenderEvent(public val partialTick: Float, public val nanos: Long) : Event