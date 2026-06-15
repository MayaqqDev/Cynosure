package dev.mayaqq.cynosure.internal

import dev.mayaqq.cynosure.core.TriState
import dev.mayaqq.cynosure.core.toTriState
import net.fabricmc.loader.api.metadata.CustomValue
import net.fabricmc.loader.api.metadata.CustomValue.CvArray
import net.fabricmc.loader.api.metadata.CustomValue.CvObject
import net.fabricmc.loader.api.metadata.ModMetadata


internal fun ModMetadata.getCynosureValue(key: String): CustomValue? {
    return when {
        containsCustomValue("cynosure:$key") -> getCustomValue("cynosure:$key")
        containsCustomValue("cynosure") -> {
            val cynData = getCustomValue("cynosure").asObject
            return if (cynData.containsKey(key)) cynData.get(key) else null
        }
        else -> null
    }
}

internal val CustomValue?.boolean: Boolean
    get() = this?.let { if(it.type == CustomValue.CvType.BOOLEAN) it.asBoolean else false } ?: false

internal val CustomValue?.numberOrNull: Number?
    get() = this?.let { if (it.type == CustomValue.CvType.NUMBER) it.asNumber else null }

internal val CustomValue?.stringOrNull: String?
    get() = this?.let { if (it.type == CustomValue.CvType.STRING) it.asString else null }

internal val CustomValue?.objectOrNull: CvObject?
    get() = this?.let { if (it.type == CustomValue.CvType.OBJECT) it.asObject else null }

internal val CustomValue?.arrayOrNull: CvArray?
    get() = this?.let { if (it.type == CustomValue.CvType.ARRAY) it.asArray else null }

internal val CustomValue?.triState: TriState
    get() = this?.let {
        if (it.type == CustomValue.CvType.BOOLEAN) it.asBoolean.toTriState() else TriState.INTERMEDIATE
    } ?: TriState.INTERMEDIATE