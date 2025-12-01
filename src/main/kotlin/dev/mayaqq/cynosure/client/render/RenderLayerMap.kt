package dev.mayaqq.cynosure.client.render

import dev.mayaqq.cynosure.internal.loadPlatform
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.material.Fluid

public interface RenderLayerMap {
    public companion object Impl : RenderLayerMap by loadPlatform()

    public fun putFluid(fluid: Fluid, renderType: RenderType)

    public fun putFluids(renderType: RenderType, vararg fluids: Fluid)
}