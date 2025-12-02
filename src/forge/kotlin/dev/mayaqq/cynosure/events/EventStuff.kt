package dev.mayaqq.cynosure.events

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.CynosureForge
import dev.mayaqq.cynosure.core.Environment
import dev.mayaqq.cynosure.core.PlatformHooks
import dev.mayaqq.cynosure.events.api.EventBus
import dev.mayaqq.cynosure.events.api.EventSubscriber
import dev.mayaqq.cynosure.events.api.MainBus
import dev.mayaqq.cynosure.events.api.CynosureEventLogger
import dev.mayaqq.cynosure.utils.asm.getClassByteArray
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode

internal val SUB_ANNOTATION_TYPE = Type.getType(EventSubscriber::class.java)


internal fun gatherEventSubscribers() {
    for(scandata in ModList.get().allScanData) {
        for(annotation in scandata.annotations) {
            if(annotation.annotationType != SUB_ANNOTATION_TYPE) continue
            try {
                // TODO: Sided event handleers
                //val side = annotation.annotationData["env"]
                //if (side != null && side != PlatformHooks.environment) continue
                val env = (annotation.annotationData["env"] as? List<ModAnnotation.EnumHolder>)?.map { Environment.valueOf(it.value) }
                if (env?.contains(PlatformHooks.environment) == false) continue

                val bus = (annotation.annotationData["bus"] as? String)
                    ?.let { Class.forName(it).kotlin.objectInstance as? EventBus } ?: MainBus

                val cr = ClassReader(CynosureForge::class.java.classLoader.getClassByteArray(annotation.memberName)!!)
                val cn = ClassNode()
                cr.accept(cn, ClassReader.SKIP_FRAMES)
                CynosureEventLogger.warn("meow {}", cn)
                CynosureEventLogger.info("Registered cynosure event subscriber ${cn.name} to bus $bus")
                bus.registerClassNode(cn)
            } catch(e: Exception) {
                CynosureEventLogger.error("Failed to subscribe to events", e)
            }
        }
    }


}