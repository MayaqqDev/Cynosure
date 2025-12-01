package dev.mayaqq.cynosure.utils.asm

import org.objectweb.asm.tree.AnnotationNode
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream




public val AnnotationNode.mappedValues: Map<String, Any?>
    get() {
        val map: MutableMap<String, Any?> = mutableMapOf()
        values?.forEachIndexed { index, value ->
            if (index % 2 == 0) map[value as String] = values[index + 1]
        }
        return map
    }

public fun String.descriptorToClassName(): String = substringAfter('L')
    .substringBefore(';')
    .replace('/', '.')

public fun ClassLoader.getClassByteArray(className: String): ByteArray? {
    return getResource(classFileFromName(className))?.openStream()?.use { inputStream ->
        val a: Int = inputStream.available()
        val outputStream = ByteArrayOutputStream(if (a < 32) 32768 else a)
        val buffer = ByteArray(8192)
        var len: Int

        while ((inputStream.read(buffer).also { len = it }) > 0) {
            outputStream.write(buffer, 0, len)
        }
        outputStream.toByteArray()
    }
    /*
    val className: String = classFileFromName(name)
    var stream = this.getResourceAsStream(className)
    if (stream == null) stream = parent.getResourceAsStream(className)
    if (stream == null) {
        return null
    }
    return readStream(stream)
     */
}

@Throws(IOException::class)
public fun readStream(stream: InputStream): ByteArray {
    val buffer = ByteArrayOutputStream()

    var nRead: Int
    val data = ByteArray(16384)

    while ((stream.read(data, 0, data.size).also { nRead = it }) != -1) {
        buffer.write(data, 0, nRead)
    }
    stream.close()
    return buffer.toByteArray()
}

public fun classFileFromName(name: String): String = name.replace('.', '/') + ".class"