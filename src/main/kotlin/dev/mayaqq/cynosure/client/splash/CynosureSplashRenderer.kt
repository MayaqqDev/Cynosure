package dev.mayaqq.cynosure.client.splash

import com.mojang.math.Axis
import dev.mayaqq.cynosure.client.splash.data.CynosureSplashLoader
import dev.mayaqq.cynosure.client.utils.pushPop
import net.minecraft.Util
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.SplashRenderer
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource


public class CynosureSplashRenderer(random: RandomSource) : SplashRenderer("") {
    private val splash: Component? = if (CynosureSplashLoader.splashes.isNotEmpty()) CynosureSplashLoader.splashes[random.nextInt(CynosureSplashLoader.splashes.size)] else null

    public override fun render(graphics: GuiGraphics, screenWidth: Int, font: Font, color: Int) {
        if (splash == null) return
        graphics.pushPop {
            translate(screenWidth.toFloat() / 2.0f + 123.0f, 69.0f, 0.0f)
            mulPose(Axis.ZP.rotationDegrees(-20.0f))
            var f = 1.8f - Mth.abs(Mth.sin((Util.getMillis() % 1000L).toFloat() / 1000.0f * (Math.PI * 2).toFloat()) * 0.1f)
            f = f * 100.0f / (font.width(splash) + 32).toFloat()
            scale(f, f, f)
            graphics.drawCenteredString(font, splash, 0, -8, 16776960 or color)
        }
    }
}