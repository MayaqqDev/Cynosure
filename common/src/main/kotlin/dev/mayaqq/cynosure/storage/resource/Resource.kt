package dev.mayaqq.cynosure.storage.resource

import com.mojang.serialization.Codec
import com.teamresourceful.bytecodecs.base.ByteCodec

public data class ResourceType<R : Resource>(
    public val codec: Codec<R>,
    public val networkCodec: ByteCodec<R>, // Is this needed?
    public val emptyStack: ResourceStack<R>
)

public interface Resource {

    public val type: ResourceType<*>

    public val isBlank: Boolean

}