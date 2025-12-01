package dev.mayaqq.cynosure.client.render

import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.material.Fluid

public object RenderLayerMapImpl : RenderLayerMap {
    override fun putFluid(fluid: Fluid, renderType: RenderType) {
        ItemBlockRenderTypes.setRenderLayer(fluid, renderType)
    }

    override fun putFluids(renderType: RenderType, vararg fluids: Fluid) {
        fluids.forEach { ItemBlockRenderTypes.setRenderLayer(it, renderType) }
    }
}