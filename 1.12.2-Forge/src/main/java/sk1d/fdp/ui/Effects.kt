/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package sk1d.fdp.ui

import dragline.ui.client.hud.element.Border
import dragline.ui.client.hud.element.Element
import dragline.ui.client.hud.element.ElementInfo
import dragline.ui.cnfont.FontLoaders
import dragline.ui.realpha
import dragline.utils.render.RenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.I18n
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import sk1d.fdp.utils.Colors
import sk1d.fdp.utils.PotionData
import sk1d.fdp.utils.Translate
import java.awt.Color
import kotlin.math.abs
import kotlin.math.max

// Skid form fdp fix by nel1y
@ElementInfo(name = "Effects")
class Effects : Element() {
    private val potionMap: MutableMap<Potion, PotionData?> = HashMap()
    override fun drawElement(): Border {
        GlStateManager.pushMatrix()
        var y = 0
        for (potionEffect in mc2.player.activePotionEffects) {
            val potion = potionEffect.potion
            val name = I18n.format(potion.name)
            val potionData: PotionData?
            if (potionMap.containsKey(potion) && potionMap[potion]!!.level == potionEffect.amplifier) potionData =
                potionMap[potion] else potionMap[potion] = PotionData(
                Translate(0f, -40f + y), potionEffect.amplifier
            ).also { potionData = it }
            var flag = true
            for (checkEffect in mc2.player.activePotionEffects) if (checkEffect.amplifier == potionData!!.level) {
                flag = false
                break
            }
            if (flag) potionMap.remove(potion)
            var potionTime: Int
            var potionMaxTime: Int
            try {
                potionTime = potionEffect.duration / if (potionEffect.amplifier >= 1) 6 else 1
                potionMaxTime =
                    if (potionEffect.potion.isInstant) 0 else if (potionEffect.duration >= Int.MAX_VALUE) 96000 else potionEffect.duration
            } catch (ignored: Exception) {
                potionTime = 100
                potionMaxTime = 1000
            }
            val lifeTime = potionTime * 60 + potionMaxTime
            if (potionData!!.getMaxTimer() == 0 || lifeTime > potionData.getMaxTimer().toDouble()) potionData.maxTimer =
                lifeTime
            var state = 0.0f
            if (lifeTime >= 0.0) state = (lifeTime / potionData.getMaxTimer().toFloat().toDouble() * 100.0).toFloat()
            val position = Math.round(potionData.translate.y + 5)
            state = max(state, 2.0f)
            potionData.translate.interpolate(0f, y.toFloat(), 0.1)
            potionData.animationX = getAnimationState(
                potionData.getAnimationX().toDouble(),
                (1.2f * state).toDouble(),
                (max(10.0f.toDouble(), abs((potionData.animationX - 1.2f * state).toDouble()) * 15.0f) * 0.3f).toDouble()
            ).toFloat()

//            System.out.println(potionData.translate.getY());
            RenderUtils.drawRect(
                0f,
                potionData.translate.y,
                120f,
                potionData.translate.y + 30f,
                realpha.reAlpha(Colors.GREY.c, 0.1f)
            )
            RenderUtils.drawRect(
                0f, potionData.translate.y, potionData.animationX, potionData.translate.y + 30f, realpha.reAlpha(
                    Color(34, 24, 20).brighter().rgb, 0.3f
                )
            )
            RenderUtils.drawShadow(0f, Math.round(potionData.translate.y).toFloat(), 120f, 30f, 255f)
            val posY = potionData.translate.y + 13f
            FontLoaders.CUSTOM18.drawString(
                name + " " + intToRomanByGreedy(potionEffect.amplifier + 1),
                29f,
                posY - mc2.fontRenderer.FONT_HEIGHT,
                realpha.reAlpha(
                    Colors.WHITE.c, 0.8f
                )
            )
            FontLoaders.CUSTOM14.drawString(
                "${potionEffect.duration / 20 / 60 }分钟", 29f, posY + 4.0f, realpha.reAlpha(
                    Color(200, 200, 200).rgb, 0.5f
                )
            )
            if (potion.hasStatusIcon()) {
                GlStateManager.pushMatrix()
                GL11.glDisable(2929)
                GL11.glEnable(3042)
                GL11.glDepthMask(false)
                OpenGlHelper.glBlendFunc(770, 771, 1, 0)
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                val statusIconIndex = potion.statusIconIndex
                mc2.textureManager.bindTexture(ResourceLocation("textures/gui/container/inventory.png"))
                mc2.ingameGUI.drawTexturedModalRect(
                    6f,
                    (position + 1).toFloat(),
                    statusIconIndex % 8 * 18,
                    198 + statusIconIndex / 8 * 18,
                    18,
                    18
                )
                GL11.glDepthMask(true)
                GL11.glDisable(3042)
                GL11.glEnable(2929)
                GlStateManager.popMatrix()
            }
            y -= 35
        }
        GlStateManager.popMatrix()
        return Border(0f, 0f, 40f, 30f)
    }

    private fun intToRomanByGreedy(num: Int): String {
        var num = num
        val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val symbols = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        val stringBuilder = StringBuilder()
        var i = 0
        while (i < values.size && num >= 0) {
            while (values[i] <= num) {
                num -= values[i]
                stringBuilder.append(symbols[i])
            }
            i++
        }
        return stringBuilder.toString()
    }

    private fun getAnimationState(animation: Double, finalState: Double, speed: Double): Double {
        var animation = animation
        val add = (0.01 * speed).toFloat()
        if (animation < finalState) {
            if (animation + add < finalState) animation += add.toDouble() else animation = finalState
        } else {
            if (animation - add > finalState) animation -= add.toDouble() else animation = finalState
        }
        return animation
    }
}