@file:ClientOnly
package dev.mayaqq.cynosure.helpers

import dev.mayaqq.cynosure.client.enviroment.ClientOnly
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import java.net.URI

public val McClient: Minecraft get() = Minecraft.getInstance()

public var Minecraft.clipboard: String get() = this.keyboardHandler.clipboard
    set(value) {
        this.keyboardHandler.clipboard = value
    }

public val Minecraft.mouse: Pair<Double, Double>
    get() = Pair(
        this.mouseHandler.xpos() * (window.guiScaledWidth / window.screenWidth.coerceAtLeast(1).toDouble()),
        this.mouseHandler.ypos() * (window.guiScaledHeight / window.screenHeight.coerceAtLeast(1).toDouble()),
    )

public fun Minecraft.openUri(uri: URI) {
    Util.getPlatform().openUri(uri)
}

public fun Minecraft.openUri(uri: String): Boolean = runCatching {
    this.openUri(URI(uri))
}.isSuccess

public fun Minecraft.schedule(action: () -> Unit) {
    this.tell { action.invoke() }
}

public fun Minecraft.setScreenAsync(screen: (previous: Screen?) -> Screen?) {
    this.schedule { this.setScreen(screen.invoke(this.screen)) }
}