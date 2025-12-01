package dev.mayaqq.cynosure.music

import dev.mayaqq.cynosure.music.MusicApi.musics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.sounds.MusicManager
import net.minecraft.core.Holder
import net.minecraft.sounds.Music
import net.minecraft.world.level.biome.Biome

public object MusicApi {
    @JvmField
    public val musics: MutableMap<Music, (LocalPlayer, MusicManager, Holder<Biome>) -> Boolean> = mutableMapOf()
}

public fun Music.register(musicWhen: (player: LocalPlayer, manager: MusicManager, biome: Holder<Biome>) -> Boolean): Music {
    musics.put(this, musicWhen)
    return this
}
