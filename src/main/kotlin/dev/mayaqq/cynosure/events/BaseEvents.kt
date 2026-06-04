package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.events.api.Event
import dev.mayaqq.cynosure.events.api.ReturningEvent
import dev.mayaqq.cynosure.events.api.RootEventClass
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder

/**
 * Common post init event, equivalent to fabric's Client and Server entrypoints and forge's FMLCommonSetupEvent
 */
public object PostInitEvent : Event

@RootEventClass
public abstract class InteractionResultEvent : ReturningEvent<InteractionResult>() {
    override var isCancelled: Boolean
        set(_) {}
        get() = result?.consumesAction() == true
}

@RootEventClass
public abstract class InteractionResultHolderEvent<T> : ReturningEvent<InteractionResultHolder<T>>() {
    override var isCancelled: Boolean
        set(_) {}
        get() = result?.result?.consumesAction() == true
}