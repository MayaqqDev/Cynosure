package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.biome.CarverRegistry
import dev.mayaqq.cynosure.events.PostInitEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.events.gatherEventSubscribers
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.PathPackResources
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraftforge.event.AddPackFindersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent
import net.minecraftforge.forgespi.locating.IModFile
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public object CynosureForge {

    init {
        MOD_BUS.register(this)
        CarverRegistry.BIOME_MODIFIER_SERIALIZERS.register(MOD_BUS)
        Cynosure.init()
    }

    @SubscribeEvent
    public fun earlyInit(event: FMLConstructModEvent) {
        event.enqueueWork(::gatherEventSubscribers)
    }

    @SubscribeEvent
    public fun lateInit(event: FMLCommonSetupEvent) {
        event.enqueueWork(PostInitEvent::post)
    }

    @SubscribeEvent
    public fun addPackFinders(event: AddPackFindersEvent) {
        for (mod in ModList.get().mods) {
            val resourcemetadata = mod.modProperties.getCynosureValue("resourcepacks") as? List<*> ?: continue
            val datametadata = mod.modProperties.getCynosureValue("datapacks") as? List<*> ?: continue
            try {
                if (event.packType == PackType.CLIENT_RESOURCES)
                    for (pack in resourcemetadata) {
                        when (pack) {
                            is String -> event.createPack(mod.owningFile.file, ResourceLocation(mod.modId, pack))
                        }
                    }
                if (event.packType == PackType.SERVER_DATA)
                    for (pack in datametadata) {
                        when (pack) {
                            is String -> event.createDataPack(mod.owningFile.file, ResourceLocation(mod.modId, pack))
                        }
                    }
            } catch (ex: Exception) {
                Cynosure.error("Failed to load ${if (event.packType == PackType.CLIENT_RESOURCES) "resourcepack" else "datapack"} for mod ${mod.modId}")
            }
        }
    }

    private fun AddPackFindersEvent.createPack(modFile: IModFile, id: ResourceLocation) {
        val resourcePath = modFile.findResource("resourcepacks/$id")

        Pack.readMetaAndCreate(
            "${id.namespace}/${id.path}",
            Component.translatable(id.toLanguageKey("resourcepack")),
            false,
            { path -> PathPackResources(path, resourcePath, true) },
            packType, Pack.Position.TOP, PackSource.BUILT_IN
        )?.let { addRepositorySource { consumer -> consumer.accept(it) } }
    }

    private fun AddPackFindersEvent.createDataPack(modFile: IModFile, id: ResourceLocation) {
        val resourcePath = modFile.findResource("datapacks/$id")

        Pack.readMetaAndCreate(
            "${id.namespace}/${id.path}",
            Component.translatable(id.toLanguageKey("datapack")),
            false,
            { path -> PathPackResources(path, resourcePath, true) },
            packType, Pack.Position.TOP, PackSource.BUILT_IN
        )?.let { addRepositorySource { consumer -> consumer.accept(it) } }
    }

    private fun Map<String, Any>.getCynosureValue(key: String): Any? {
        if (containsKey("cynosure:$key")) {
            return get("cynosure:$key")
        } else {
            if (containsKey("cynosure")) {
                val cynosureData = get("cynosure") as Map<String, Any>
                return cynosureData[key]
            }
        }
        return null
    }
}