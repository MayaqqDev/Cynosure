package dev.mayaqq.cynosure.client.events.screen

import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.RootEventClass
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.screens.Screen

@RootEventClass
public sealed class ScreenEvents(
    public val minecraft: Minecraft,
    public val screen: Screen,
    public val listeners: List<GuiEventListener>
) : Event() {
    public class BeforeInit(
        minecraft: Minecraft,
        screen: Screen,
        listeners: List<GuiEventListener>
    ) : ScreenEvents(minecraft, screen, listeners)

    public class AfterInit(
        minecraft: Minecraft,
        screen: Screen,
        listeners: List<GuiEventListener>
    ) : ScreenEvents(minecraft, screen, listeners)
}
