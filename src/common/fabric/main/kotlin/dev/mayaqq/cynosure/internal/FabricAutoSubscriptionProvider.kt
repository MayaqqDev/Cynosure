package dev.mayaqq.cynosure.internal

import dev.mayaqq.cynosure.events.api.CynosureAutoSubscriptionService
import dev.mayaqq.cynosure.events.api.EventSubscriber
import invoke.kitty.kritter.platform.ENVIRONMENT
import invoke.kitty.kritter.platform.fabric.kritterMod
import invoke.kitty.kritter.utils.sequence.mapMulti
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import net.fabricmc.loader.api.FabricLoader
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.walk

private const val AUTOSUB_KEY = "autosubscription"

@ExperimentalPathApi
internal class FabricAutoSubscriptionProvider : CynosureAutoSubscriptionService {
    override fun gatherEventSubscribers(): Flow<CynosureAutoSubscriptionService.SubscriberData> =
        FabricLoader.getInstance()
            .allMods
            .asFlow()
            .filter { it.metadata.getCynosureValue(AUTOSUB_KEY).boolean }
            .mapMulti { mod ->
                val classPath = mod.rootPaths.asSequence().flatMap { path ->
                    path.walk()
                        .filter { it.extension == "class" && it.name != "module-info.class" && it.name != "package-info.class" }
                        .map { path.relativize(it) }
                }

                for (path in classPath) {
                    val clazz = try {
                        Class.forName(
                            path.toString().replace('/', '.').dropLast(6),
                            false,
                            FabricAutoSubscriptionProvider::class.java.classLoader
                        )
                    } catch (ex: ClassNotFoundException) {
                        continue;
                    }

                    val subscriber = clazz.getAnnotation(EventSubscriber::class.java) ?: continue
                    if (ENVIRONMENT !in subscriber.value) continue
                    emit(CynosureAutoSubscriptionService.SubscriberData(mod.kritterMod, clazz))
                }
            }
}