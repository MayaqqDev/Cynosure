package dev.mayaqq.cynosure

import dev.mayaqq.cynosure.core.Environment
import dev.mayaqq.cynosure.core.PlatformHooks
import dev.mayaqq.cynosure.events.api.EventBus
import dev.mayaqq.cynosure.events.api.EventSubscriber
import dev.mayaqq.cynosure.events.api.MainBus
import dev.mayaqq.cynosure.events.api.CynosureEventLogger
import dev.mayaqq.cynosure.internal.boolean
import dev.mayaqq.cynosure.internal.getCynosureValue
import dev.mayaqq.cynosure.utils.asm.descriptorToClassName
import dev.mayaqq.cynosure.utils.asm.mappedValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.impl.launch.FabricLauncherBase
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension
import kotlin.io.path.walk

private const val AUTOSUB_KEY = "autosubscription"

private val AUTOSUB_ANNOTATION = EventSubscriber::class.qualifiedName!!

@CynosureInternal
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPathApi::class)
internal fun onPreLaunch() {
    val flow = FabricLoader.getInstance()
        .allMods
        .asFlow()
        .filter {
            it.metadata.getCynosureValue(AUTOSUB_KEY).boolean
        }
        .flatMapMerge(32) { mod: ModContainer ->
            mod.rootPaths.asFlow().flatMapMerge(32) { path ->
                path.walk().asFlow()
                    .filter { it.extension == "class" }
                    .map { mod to path.relativize(it) }
            }
        }
        .onEach { (mod, classPath) -> subscribeClasses(mod, classPath) }

    runBlocking(Dispatchers.Default) {
        launch { flow.collect() }
    }

}

@CynosureInternal
private fun subscribeClasses(mod: ModContainer, classPath: Path) {
    val className = classPath.joinToString(".").dropLast(6)
    val reader = ClassReader(FabricLauncherBase.getLauncher().getClassByteArray(className, false))
    val cn = ClassNode()

    reader.accept(cn, ClassReader.SKIP_FRAMES)

    val annotation = cn.visibleAnnotations
        ?.find { it.desc.descriptorToClassName() == AUTOSUB_ANNOTATION }
        ?: return

    val subscriberData = annotation.mappedValues

    val env = (subscriberData["env"] as? List<Array<String>>)?.map { Environment.valueOf(it[1]) }
    if (env?.contains(PlatformHooks.environment) == false) return

    try {
        val bus =
            (subscriberData["bus"] as? Type)?.let {
                Class.forName(it.className).kotlin.objectInstance as? EventBus
            } ?: MainBus

        bus.registerClassNode(cn)

        if (FabricLoader.getInstance().isDevelopmentEnvironment || System.getProperty("cynosure.logEventSubscribers").toBoolean())
            CynosureEventLogger.info("Registered cynosure event subscriber for mod ${mod.metadata.id}: $className to bus $bus")

    } catch (e: Exception) {
        CynosureEventLogger.error("Failed to register event bus subscriber for mod ${mod.metadata.id}", e)
    }
}