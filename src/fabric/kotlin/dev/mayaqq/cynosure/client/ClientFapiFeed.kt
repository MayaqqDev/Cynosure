package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.CynosureInternal
import dev.mayaqq.cynosure.client.events.ClientTickEvent
import dev.mayaqq.cynosure.client.events.CoreShaderRegistrationEvent
import dev.mayaqq.cynosure.client.events.render.EndHudRenderEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.world.LevelEvent
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.Minecraft

internal object ClientFapiFeed {
    @CynosureInternal
    fun feed() {

        // Registration
        CoreShaderRegistrationCallback.EVENT.register {
            CoreShaderRegistrationEvent(fun(id, format, callback) = it.register(id, format, callback))
                .post { Cynosure.error("Error registering shaders", it) }
        }

        ClientCommandRegistrationCallback.EVENT.register { commandDispatcher, commandBuildContext ->

        }

        // Tick
        ClientTickEvents.START_CLIENT_TICK.register { ClientTickEvent.Begin.post() }
        ClientTickEvents.END_CLIENT_TICK.register { ClientTickEvent.End.post() }
        ClientTickEvents.START_WORLD_TICK.register { LevelEvent.BeginTick(it).post(context = "client") }
        ClientTickEvents.END_WORLD_TICK.register { LevelEvent.EndTick(it).post(context = "client") }

        // Rendering
        HudRenderCallback.EVENT.register { graphics, float ->
            EndHudRenderEvent(Minecraft.getInstance().gui, graphics, float).post()
        }
    }
}
