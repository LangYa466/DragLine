/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.ui.client.hud.element.elements

import dragline.ui.client.hud.element.Border
import dragline.ui.client.hud.element.Element
import dragline.ui.client.hud.element.ElementInfo
import dragline.ui.client.hud.element.Side
import dragline.ui.cnfont.FontLoaders
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import me.nelly.utils.RoundedUtil
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

/**
 * CustomHUD effects element
 *
 * Shows a list of active potion effects
 */
@ElementInfo(name = "Effects2")
class Effects2(x: Double = 2.0, y: Double = 10.0, scale: Float = 1F,
              side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {

    private val shadow = BoolValue("Shadow", true)
    private val radiusValue = FloatValue("Radius", 2.0f, 0.0f, 10.0f)

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        var y = 0F
        var width = 0F

        val fontRenderer = FontLoaders.CUSTOM18


        for (effect in mc.thePlayer!!.activePotionEffects) {
            val potion = functions.getPotionById(effect.potionID)

            val number = when {
                effect.amplifier == 1 -> "II"
                effect.amplifier == 2 -> "III"
                effect.amplifier == 3 -> "IV"
                effect.amplifier == 4 -> "V"
                effect.amplifier == 5 -> "VI"
                effect.amplifier == 6 -> "VII"
                effect.amplifier == 7 -> "VIII"
                effect.amplifier == 8 -> "IX"
                effect.amplifier == 9 -> "X"
                effect.amplifier > 10 -> "X+"
                else -> "I"
            }

            val name = "${functions.formatI18n(potion.name)} $number§f: §7${effect.getDurationString()}"
            val stringWidth = fontRenderer.getStringWidth(name).toFloat()

            if (width < stringWidth)
                width = stringWidth

            GlStateManager.resetColor()
            RoundedUtil.drawRound(
                x.toFloat(),
                y,
                stringWidth,
                fontRenderer.FONT_HEIGHT.toFloat(),
                radiusValue.get(),
                Color(0, 0, 0, 60)
            )

            if(shadow.get()) {
                RenderUtils.drawShadow(
                    x.toFloat(),
                    y,
                    stringWidth,
                    fontRenderer.FONT_HEIGHT.toFloat(),
                    255f
                )
            }

            fontRenderer.drawString(name, -stringWidth, y, potion.liquidColor, shadow.get())
            y -= fontRenderer.FONT_HEIGHT
        }


        if (width == 0F)
            width = 40F

        if (y == 0F)
            y = -10F

        return Border(2F, fontRenderer.FONT_HEIGHT.toFloat(), -width - 2F, y + fontRenderer.FONT_HEIGHT - 2F)
    }
}