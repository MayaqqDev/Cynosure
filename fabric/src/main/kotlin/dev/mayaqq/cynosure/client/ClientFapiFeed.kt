package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.client.events.ClientTickEvent
import dev.mayaqq.cynosure.client.events.render.EndHudRenderEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.world.LevelEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.client.Minecraft

internal object ClientFapiFeed {
    fun feed() {
        HudRenderCallback.EVENT.register { graphics, float ->
            EndHudRenderEvent(Minecraft.getInstance().gui, graphics, float).post()
        }
        ClientTickEvents.START_CLIENT_TICK.register { ClientTickEvent.Begin.post() }
        ClientTickEvents.END_CLIENT_TICK.register { ClientTickEvent.End.post() }
        ClientTickEvents.START_WORLD_TICK.register { LevelEvent.BeginTick(it).post(context = "client") }
        ClientTickEvents.END_WORLD_TICK.register { LevelEvent.EndTick(it).post(context = "client") }
        CommandRegistrationCallback.EVENT.register { }
    }
}
