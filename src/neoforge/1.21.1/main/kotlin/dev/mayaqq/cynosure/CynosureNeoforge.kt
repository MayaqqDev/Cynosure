package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.biome.CarverRegistry
import dev.mayaqq.cynosure.client.CynosureNeoforgeClient
import dev.mayaqq.cynosure.client.events.CynosureForgeClientEvents
import dev.mayaqq.cynosure.client.internal.ClientHooksEventListener
import dev.mayaqq.cynosure.client.keymapping.KeyMappingRegistryEventSubscriber
import dev.mayaqq.cynosure.client.tooltips.ClientTooltipFactoriesImpl
import dev.mayaqq.cynosure.core.identifier
import dev.mayaqq.cynosure.events.ForgeEvents
import dev.mayaqq.cynosure.events.PostInitEvent
import dev.mayaqq.cynosure.events.api.post
import dev.mayaqq.cynosure.text.Text
import invoke.kitty.kritter.events.LateInitEvent
import invoke.kitty.kritter.platform.Mod
import invoke.kitty.kritter.platform.forge.EntrypointHandler
import invoke.kitty.kritter.platform.forge.eventBus
import invoke.kitty.kritter.utils.clientOnly
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
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddPackFindersEvent
import net.neoforged.neoforgespi.language.IModInfo
import net.neoforged.neoforgespi.locating.IModFile
import java.util.Optional

@EntrypointHandler("init")
fun init(mod: Mod) {
    CynosureForgeLike.init()
    CarverRegistry.BIOME_MODIFIER_SERIALIZERS.register(mod.eventBus)
    LateInitEvent.subscribe { CynosureNeoforge.modPropertiesInit() }
    mod.eventBus.let { modBus ->
        modBus.register(CynosureNeoforge)
        clientOnly {
            modBus.register(CynosureNeoforgeClient)
            modBus.register(ClientHooksEventListener)
            modBus.register(KeyMappingRegistryEventSubscriber)
            modBus.register(ClientTooltipFactoriesImpl)
        }
    }
    NeoForge.EVENT_BUS.let { gameBus ->
        gameBus.register(ForgeEvents)
        clientOnly {
            gameBus.register(CynosureForgeClientEvents)
        }
    }
}

public object CynosureNeoforge {

    @SubscribeEvent
    public fun lateInit(event: FMLCommonSetupEvent) {
        event.enqueueWork(PostInitEvent::post)
    }

    private val resourcemetadata: MutableList<Pair<IModInfo, List<*>>> = mutableListOf()
    private val datametadata: MutableList<Pair<IModInfo, List<*>>> = mutableListOf()

    private val packSource = PackSource.create(PackSource.decorateWithSource("pack.source.cynosure"), false)

    fun modPropertiesInit() {
        for (mod in ModList.get().mods) {
            (mod.modProperties.getCynosureValue("resourcepacks") as? List<*>)?.let { data ->
                resourcemetadata.add(mod to data)
                Cynosure.info("Resource data found in ${mod.modId}: $data")
            }
            (mod.modProperties.getCynosureValue("datapacks") as? List<*>)?.let { data ->
                datametadata.add(mod to data)
                Cynosure.info("Datapack data found in ${mod.modId}: $data")
            }
        }
    }

    @SubscribeEvent
    public fun addPackFinders(event: AddPackFindersEvent) {
        resourcemetadata.forEach { (mod, data) ->
            try {
                if (event.packType == PackType.CLIENT_RESOURCES)
                    for (pack in data) {
                        when (pack) {
                            is String -> event.createPack(mod.owningFile.file,
                                identifier(mod.modId, pack)
                            )
                        }
                    }
            } catch (ex: Exception) {
                Cynosure.error("Failed to load resourcepack for ${mod.modId}")
            }
        }
        datametadata.forEach { (mod, data) ->
            try {
                if (event.packType == PackType.SERVER_DATA)
                    for (pack in data) {
                        when (pack) {
                            is String -> event.createDataPack(mod.owningFile.file,
                                identifier(mod.modId, pack)
                            )
                        }
                    }
            } catch (ex: Exception) {
                Cynosure.error("Failed to load datapack for ${mod.modId}")
            }
        }
    }

    private fun AddPackFindersEvent.createPack(modFile: IModFile, id: ResourceLocation) {
        val resourcePath = modFile.findResource("resourcepacks/${id.path}")
        val locationInfo = PackLocationInfo(
            "${id.namespace}/${id.path}",
            Component.translatable(id.toLanguageKey("resourcepack")),
            packSource,
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
        val resourcePath = modFile.findResource("datapacks/${id.path}")
        val locationInfo = PackLocationInfo(
            "${id.namespace}/${id.path}",
            Component.translatable(id.toLanguageKey("datapack")),
            packSource,
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