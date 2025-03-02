package dev.mayaqq.cynosure.client.models.baked

internal const val STRIDE: Int = 6;
internal const val PACK: Float = 127.0f
internal const val UNPACK: Float = 1.0f / PACK

@JvmInline
value class Mesh(private val data: IntArray) {

    companion object {
        val EMPTY: Mesh = Mesh(IntArray(0))
    }

    val vertexCount: Int get() = data.size / STRIDE

    fun x(index: Int): Float = Float.fromBits(data[index * STRIDE])

    fun y(index: Int): Float = Float.fromBits(data[index * STRIDE + 1])

    fun z(index: Int): Float = Float.fromBits(data[index * STRIDE + 2])

    fun u(index: Int): Float = Float.fromBits(data[index * STRIDE + 3])

    fun v(index: Int): Float = Float.fromBits(data[index * STRIDE + 4])

    fun normalX(index: Int): Float = ((data[index * STRIDE + 5] and 0xFF).toByte()) * UNPACK

    fun normalY(index: Int): Float = (((data[index * STRIDE + 5] ushr 8) and 0xFF).toByte()) * UNPACK

    fun normalZ(index: Int): Float = (((data[index * STRIDE + 5] ushr 16) and 0xFF).toByte()) * UNPACK
}
