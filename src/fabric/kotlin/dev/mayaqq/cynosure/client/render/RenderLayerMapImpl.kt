package dev.mayaqq.cynosure.client.render

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.material.Fluid

public object RenderLayerMapImpl : RenderLayerMap {
    override fun putFluid(fluid: Fluid, renderType: RenderType) {
        BlockRenderLayerMap.INSTANCE.putFluid(fluid, renderType)
    }

    override fun putFluids(renderType: RenderType, vararg fluids: Fluid) {
        BlockRenderLayerMap.INSTANCE.putFluids(renderType, *fluids)
    }
}