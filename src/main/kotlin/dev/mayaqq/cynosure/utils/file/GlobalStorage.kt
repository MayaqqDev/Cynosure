package dev.mayaqq.cynosure.utils.file

import dev.mayaqq.cynosure.Cynosure
import dev.mayaqq.cynosure.MODID
import dev.mayaqq.cynosure.core.gameDir
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

    private var cache: Path
    private var data: Path

    init {
        val os = System.getProperty("os.name")

        val possibleCache: Path
        val possibleData: Path

        if (os.startsWith("Windows")) {
            possibleCache = Path.of(System.getenv("LOCALAPPDATA"), ".$MODID", "cache")
            possibleData = Path.of(System.getenv("LOCALAPPDATA"), MODID, "data")
        } else if (os.startsWith("Mac OS X") || os.startsWith("Darwin")) {
            possibleCache = Path.of(System.getProperty("user.home"), "Library", "Caches", MODID)
            possibleData = Path.of(System.getProperty("user.home"), "Library", "Application Support", MODID)
        } else {
            possibleCache = System.getenv("XDG_CACHE_HOME")?.let { Path.of(it, MODID) } ?: Path.of(System.getProperty("user.home"), ".cache", MODID)
            possibleData = System.getenv("XDG_DATA_HOME")?.let { Path.of(it, MODID) } ?: Path.of(System.getProperty("user.home"), ".local", "share", MODID)
        }

        try {
            if (possibleCache.notExists()) possibleCache.createDirectory()
            if (possibleData.notExists()) possibleData.createDirectory()
            cache = possibleCache
            data = possibleData
        } catch (e: Exception) {
            val localCacheDir = gameDir.resolve(".cache")
            val localDataDir = gameDir.resolve("moddata")
            cache = localCacheDir.resolve(MODID)
            data = localDataDir.resolve(MODID)
            try {
                if (localCacheDir.notExists()) localCacheDir.createDirectory()
                if (localDataDir.notExists()) localDataDir.createDirectory()
                if (cache.notExists()) cache.createDirectory()
                if (data.notExists()) data.createDirectory()
            } catch (e: Exception) {
                Cynosure.error("I tried so hard to make Cynosure Global Storage directories, yet, I failed: ", e)
            }
        }

        try {

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