package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.biome.CarverRegistry
import dev.mayaqq.cynosure.core.identifier
import dev.mayaqq.cynosure.events.PostInitEvent
import dev.mayaqq.cynosure.events.api.post
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackSelectionConfig
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.PathPackResources
import net.minecraft.server.packs.repository.BuiltInPackSource
import net.minecraft.server.packs.repository.KnownPack
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.AddPackFindersEvent
import net.neoforged.neoforgespi.locating.IModFile
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.util.Optional

@Mod(MODID)
@EventBusSubscriber
public object CynosureNeoforge {
    init {
        CynosureForgeLike.init()
        CarverRegistry.BIOME_MODIFIER_SERIALIZERS.register(MOD_BUS)
    }

    @SubscribeEvent
    public fun lateInit(event: FMLCommonSetupEvent) {
        event.enqueueWork(PostInitEvent::post)
    }

    @SubscribeEvent
    public fun addPackFinders(event: AddPackFindersEvent) {
        //TODO: test this whole thing.
        for (mod in ModList.get().mods) {
            val resourcemetadata = mod.modProperties.getCynosureValue("resourcepacks") as? List<*> ?: continue
            val datametadata = mod.modProperties.getCynosureValue("datapacks") as? List<*> ?: continue
            try {
                if (event.packType == PackType.CLIENT_RESOURCES)
                    for (pack in resourcemetadata) {
                        when (pack) {
                            is String -> event.createPack(mod.owningFile.file,
                                identifier(mod.modId, pack)
                            )
                        }
                    }
                if (event.packType == PackType.SERVER_DATA)
                    for (pack in datametadata) {
                        when (pack) {
                            is String -> event.createDataPack(mod.owningFile.file,
                                identifier(mod.modId, pack)
                            )
                        }
                    }
            } catch (ex: Exception) {
                Cynosure.error("Failed to load ${if (event.packType == PackType.CLIENT_RESOURCES) "resourcepack" else "datapack"} for mod ${mod.modId}")
            }
        }
    }

    private fun AddPackFindersEvent.createPack(modFile: IModFile, id: ResourceLocation) {
        val resourcePath = modFile.findResource("resourcepacks/$id")
        val locationInfo = PackLocationInfo(
            "${id.namespace}/${id.path}",
            Component.translatable(id.toLanguageKey("resourcepack")),
            PackSource.BUILT_IN,
            Optional.of(
                KnownPack(
                    id.namespace,
                    id.path,
                    modFile.modInfos[0].version.toString()
                )
            )
        )

        Pack.readMetaAndCreate(
            locationInfo,
            BuiltInPackSource.fixedResources(
                PathPackResources(
                    locationInfo,
                    resourcePath
                )
            ),
            packType,
            PackSelectionConfig(
                false,
                Pack.Position.TOP,
                false
            )
        ).let { addRepositorySource { consumer -> consumer.accept(it) } }
    }

    private fun AddPackFindersEvent.createDataPack(modFile: IModFile, id: ResourceLocation) {
        val resourcePath = modFile.findResource("datapacks/$id")
        val locationInfo = PackLocationInfo(
            "${id.namespace}/${id.path}",
            Component.translatable(id.toLanguageKey("datapack")),
            PackSource.BUILT_IN,
            Optional.of(
                KnownPack(
                    id.namespace,
                    id.path,
                    modFile.modInfos[0].version.toString()
                )
            )
        )

        Pack.readMetaAndCreate(
            locationInfo,
            BuiltInPackSource.fixedResources(
                PathPackResources(
                    locationInfo,
                    resourcePath
                )
            ),
            packType,
            PackSelectionConfig(
                false,
                Pack.Position.TOP,
                false
            )
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