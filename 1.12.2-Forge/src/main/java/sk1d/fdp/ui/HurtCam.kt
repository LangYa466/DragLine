/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package sk1d.fdp.ui

import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.Render2DEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.render.ColorUtils
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.IntegerValue
import dragline.value.ListValue
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.minecraft.network.play.server.SPacketEntityStatus
import java.awt.Color

@ModuleInfo(name = "HurtCam", category = ModuleCategory.CLIENT, description = "FDP", canEnable = false)
class HurtCam : Module() {
    val modeValue = ListValue("Mode", arrayOf("Vanilla", "Cancel", "FPS"), "Vanilla")
    private val colorRedValue = IntegerValue("R", 255, 0, 255).displayable { modeValue.equals("FPS") }
    private val colorGreenValue = IntegerValue("G", 0, 0, 255).displayable { modeValue.equals("FPS") }
    private val colorBlueValue = IntegerValue("B", 0, 0, 255).displayable { modeValue.equals("FPS") }
    private val colorRainbow = BoolValue("Rainbow", false).displayable { modeValue.equals("FPS") }
    private val timeValue = IntegerValue("FPSTime", 1000, 0, 1500).displayable { modeValue.equals("FPS") }
    private val fpsHeightValue = IntegerValue("FPSHeight", 25, 10, 50).displayable { modeValue.equals("FPS") }

    private var hurt = 0L

    @EventTarget
    fun onRender2d(event: Render2DEvent) {
        if (hurt == 0L) return

        val passedTime = System.currentTimeMillis() - hurt
        if (passedTime > timeValue.get()) {
            hurt = 0L
            return
        }

        val color = getColor((((timeValue.get() - passedTime) / timeValue.get().toFloat()) * 255).toInt())
        val color1 = getColor(0)
        val width = event.scaledResolution.scaledWidth_double
        val height = event.scaledResolution.scaledHeight_double

        RenderUtils.drawGradientSidewaysV(0.0, 0.0, width, fpsHeightValue.get().toDouble(), color.rgb, color1.rgb)
        RenderUtils.drawGradientSidewaysV(0.0, height - fpsHeightValue.get(), width, height, color1.rgb, color.rgb)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()

        when (modeValue.get().toLowerCase()) {
            "fps" -> {
                if (packet is SPacketEntityStatus) {
                    if (packet.opCode.toInt() == 2 && mc2.player.equals(packet.getEntity(mc2.world))) {
                        hurt = System.currentTimeMillis()
                    }
                }
            }
        }
    }

    private fun getColor(alpha: Int): Color {
        return if (colorRainbow.get()) ColorUtils.reAlpha(ColorUtils.rainbow(), alpha) else Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), alpha)
    }

    // always handle event
    override fun handleEvents() = true
}