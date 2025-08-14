package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.client.splash.CynosureSplashRenderer
import dev.mayaqq.cynosure.events.PostInitEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.fapiFeed
import dev.mayaqq.cynosure.internal.arrayOrNull
import dev.mayaqq.cynosure.internal.getCynosureValue
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.metadata.CustomValue.CvArray
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import kotlin.reflect.typeOf

internal object CynosureFabric {
    fun init() {
        Cynosure.init()
        // Didnt put these in fapi feed cs they're more for internal stuff
        fapiFeed()

        for (mod in FabricLoader.getInstance().allMods) {
            val metadata = mod.metadata
            metadata.getCynosureValue("resourcepacks").arrayOrNull?.let { loadModPacks(mod, it) }
            metadata.getCynosureValue("datapacks").arrayOrNull?.let { loadModDatapacks(mod, it) }
        }
    }

    fun lateinit() {
        val a = typeOf<MutableMap<out String, Int>>()
        PostInitEvent.post()
    }

    private fun loadModPacks(mod: ModContainer, packs: CvArray) {
        val metadata = mod.metadata
        for (pack in packs) {
            try {
                val packId = ResourceLocation(metadata.id, pack.asString)
                ResourceManagerHelper.registerBuiltinResourcePack(
                    packId, mod,
                    Component.translatable(packId.toLanguageKey("resourcepacks")),
                    ResourcePackActivationType.NORMAL
                )
            } catch (ex: Exception) {
                Cynosure.error("Failed to load built in resourcepack for mod ${metadata.id}")
            }
        }
    }

    private fun loadModDatapacks(mod: ModContainer, packs: CvArray) {
        val metadata = mod.metadata
        for (pack in packs) {
            try {
                val packId = ResourceLocation(metadata.id, pack.asString)
                ResourceManagerHelper.registerBuiltinResourcePack(
                    packId, mod,
                    Component.translatable(packId.toLanguageKey("datapacks")),
                    ResourcePackActivationType.NORMAL
                )
            } catch (ex: Exception) {
                Cynosure.error("Failed to load built in datapack for mod ${metadata.id}")
            }
        }
    }
}