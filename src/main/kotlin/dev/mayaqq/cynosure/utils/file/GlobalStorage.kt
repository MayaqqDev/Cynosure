package dev.mayaqq.cynosure.utils.file

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.MODID
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.writeText

public object GlobalStorage {

    private const val README = """
        This Directory was created by Cynosure Library Mod.
        This directory is used to store global files of Minecraft Mods.
    """

    private val cache: Path
    private val data: Path

    init {
        val os = System.getProperty("os.name")
        if (os.startsWith("Windows")) {
            cache = Path.of(System.getenv("LOCALAPPDATA"), ".$MODID", "cache")
            data = Path.of(System.getenv("LOCALAPPDATA"), MODID, "data")
        } else if (os.startsWith("Mac OS X") || os.startsWith("Darwin")) {
            cache = Path.of(System.getProperty("user.home"), "Library", "Caches", MODID)
            data = Path.of(System.getProperty("user.home"), "Library", "Application Support", MODID)
        } else {
            cache = System.getenv("XDG_CACHE_HOME")?.let { Path.of(it, MODID) } ?: Path.of(System.getProperty("user.home"), ".cache", MODID)
            data = System.getenv("XDG_DATA_HOME")?.let { Path.of(it, MODID) } ?: Path.of(System.getProperty("user.home"), ".local", "share", MODID)
        }

        try {
            if (cache.notExists()) cache.createDirectory()
            if (data.notExists()) data.createDirectory()

            var readme = cache.resolve("README.txt")
            if (!readme.exists()) {
                readme.writeText(README)
            }

            readme = data.resolve("README.txt")
            if (!readme.exists()) {
                readme.writeText(README)
            }
        } catch (e: IOException) {
            Cynosure.error("Failed to create global storage directories", e)
        }
    }

    public fun getCache(modid: String): Path = cache.resolve(modid)
    public fun getData(modid: String): Path = data.resolve(modid)
}