package dev.mayaqq.cynosure.client.models.baked

internal const val STRIDE: Int = 6;
internal const val PACK: Float = 127.0f
internal const val UNPACK: Float = 1.0f / PACK

@JvmInline
public value class Mesh(private val data: IntArray) {

    public companion object {
        public val EMPTY: Mesh = Mesh(IntArray(0))
    }

    public val vertexCount: Int get() = data.size / STRIDE

    public fun x(index: Int): Float = Float.fromBits(data[index * STRIDE])

    public fun y(index: Int): Float = Float.fromBits(data[index * STRIDE + 1])

    public fun z(index: Int): Float = Float.fromBits(data[index * STRIDE + 2])

    public fun u(index: Int): Float = Float.fromBits(data[index * STRIDE + 3])

    public fun v(index: Int): Float = Float.fromBits(data[index * STRIDE + 4])

    public fun normalX(index: Int): Float = ((data[index * STRIDE + 5] and 0xFF).toByte()) * UNPACK

    public fun normalY(index: Int): Float = (((data[index * STRIDE + 5] ushr 8) and 0xFF).toByte()) * UNPACK

    public fun normalZ(index: Int): Float = (((data[index * STRIDE + 5] ushr 16) and 0xFF).toByte()) * UNPACK
}
