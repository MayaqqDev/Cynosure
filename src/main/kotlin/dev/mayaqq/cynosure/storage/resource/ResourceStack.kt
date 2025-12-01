package dev.mayaqq.cynosure.storage.resource

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.mayaqq.cynosure.core.codecs.fieldOf
import dev.mayaqq.cynosure.core.codecs.forGetter

public class ResourceStack<R : Resource>(
    public val resource: R,
    public val amount: Long
) {
    public companion object {



        public fun <R : Resource> codec(type: ResourceType<R>): Codec<ResourceStack<R>> = RecordCodecBuilder.create { it.group(
            type.codec fieldOf "resource" forGetter ResourceStack<R>::resource,
            Codec.LONG fieldOf "amount" forGetter ResourceStack<R>::amount,
        ).apply(it, ::ResourceStack) }
    }

}