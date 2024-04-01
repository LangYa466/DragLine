/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte
 */

package dragline.ui.client.hud.element.elements

import dragline.DragLine
import dragline.api.minecraft.client.entity.IEntityLivingBase
import dragline.api.minecraft.client.gui.IFontRenderer
import dragline.api.minecraft.util.IResourceLocation
import dragline.features.module.modules.combat.KillAura
import dragline.features.module.modules.client.HUD
import dragline.ui.client.hud.element.Border
import dragline.ui.client.hud.element.Element
import dragline.ui.client.hud.element.ElementInfo
import dragline.ui.font.Fonts
import dragline.utils.extensions.getDistanceToEntityBox
import dragline.utils.render.ColorUtils
import dragline.value.*
import dragline.utils.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.pow


/**
 * A target hud
 */
@ElementInfo(name = "Target")
class Target : Element() {

    private val modeValue =
        ListValue("Style", arrayOf("Novoline", "LiquidBounce", "Astro", "WTF", "Nameless"), "LiquidBounce")
    private val cfont = BoolValue("CFont", false)
    private val outline = BoolValue("Outline", false)
    private val animSpeedValue = IntegerValue("AnimSpeed",10,5,20)
    private val fontValue = FontValue("Rise-Font", Fonts.font40)

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val fadeSpeed = FloatValue("HP-FadeSpeed", 2F, 1F, 9F)
    private val fadeSpeed2 = FloatValue("FadeSpeed", 2F, 1F, 9F)
    private var prevTarget: EntityLivingBase?=null
    private var lastHealth=20F
    private var lastChangeHealth=20F
    private var changeTime=System.currentTimeMillis()
    private var easingHP = 0f

    private var easingHealth: Float = 0F

    var fontRenderer: IFontRenderer = Fonts.font40

    private var mainTarget: IEntityLivingBase? = null
    private var animProgress = 0F
    private fun getHealth(entity: EntityLivingBase?):Float{
        return if(entity==null || entity.isDead){ 0f }else{ entity.health }
    }
    override fun drawElement(): Border {
        fontRenderer = if (this.cfont.get())
            Fonts.font35
        else
            mc.fontRendererObj
        val time=System.currentTimeMillis()
        val ka = DragLine.moduleManager.getModule(KillAura::class.java) as KillAura
        val actualTarget = ka.target

            ?: if (classProvider.isGuiHudDesigner(mc.currentScreen)) mc.thePlayer
            else null

        var width = when (modeValue.get().toLowerCase()) {
            "novoline" -> 80F
            "astro" -> 90F
            "wtf" -> 100F
            "nameless" -> 128F
            else -> 100F
        }
        val height: Float = when (modeValue.get().toLowerCase()) {
            "novoline" -> 34F
            "astro" -> 40F
            "wtf" -> 44F
            "nameless" -> 36F
            else -> 36F
        }

        // animProgress += ((1 - animProgress) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime * if (actualTarget != null) -1F else 1F

        val num = if (actualTarget != null) 0F else 1F
        animProgress += ((num - animProgress) / 2.0F.pow(10.0F - fadeSpeed2.get())) * RenderUtils.deltaTime

        animProgress = animProgress.coerceIn(0F, 1F)

        if (actualTarget != null)
            mainTarget = actualTarget
        else if (animProgress >= 1F) {
            mainTarget = null
        }

        if (mainTarget == null) {
            easingHealth = 0F
            return Border(0F, 0F, width, height)
        }
        if(getHealth(prevTarget)!=lastHealth){
            lastChangeHealth=lastHealth
            lastHealth=getHealth(prevTarget)
            changeTime=time
        }
        val nowAnimHP=if((time-(animSpeedValue.get()*50))<changeTime){
            getHealth(prevTarget)+(lastChangeHealth-getHealth(prevTarget))*(1-((time-changeTime)/(animSpeedValue.get()*50F)))
        }else{
            getHealth(prevTarget)
        }
        width = when (modeValue.get().toLowerCase()) {
            "novoline" -> (38 + (mainTarget!!.name?.let(mc.fontRendererObj::getStringWidth) ?: 0)).coerceAtLeast(80)
                .toFloat()

            "astro" -> (38 + (mainTarget!!.name?.let(mc.fontRendererObj::getStringWidth) ?: 0)).coerceAtLeast(90)
                .toFloat()

            "wtf", "nameless" -> (38 + (mainTarget!!.name?.let(mc.fontRendererObj::getStringWidth) ?: 0)).coerceAtLeast(
                100
            ).toFloat()

            else -> (38 + (mainTarget!!.name?.let(mc.fontRendererObj::getStringWidth) ?: 0)).coerceAtLeast(100)
                .toFloat()
        }

        val calcScaleX = animProgress
        val calcScaleY = animProgress
        val calcTranslateX = width / 2F * calcScaleX
        val calcTranslateY = height / 2F * calcScaleY

        GL11.glPushMatrix()
        GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
        GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
        when (modeValue.get().toLowerCase()) {
            "novoline" -> novoline(mainTarget!!, width, height)
            "astro" -> astro(mainTarget!!, width, height)
            "wtf" -> wtf(mainTarget!!, width, height)
            "nameless" -> nameless(mainTarget!!, width, height)
            "tenacity" -> drawTenacity(prevTarget!!)
            else -> liquidbounce(mainTarget!!, width, height)
        }

        GL11.glPopMatrix()

        GlStateManager.resetColor()
        return Border(0F, 0F, width, height)
    }
    private fun drawTenacity(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(75)
        RenderUtils.drawCircleRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)
        var playerInfo = mc.netHandler.getPlayerInfo(mc.thePlayer!!.uniqueID)

        // circle player avatar
        val locationSkin = playerInfo!!.locationSkin

        mc.textureManager.bindTexture(locationSkin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)

        // info text
        font.drawCenteredString(target.name, 40 + (additionalWidth / 2f), 5f, Color.WHITE.rgb, false)
        "${decimalFormat.format((easingHP / target.maxHealth) * 100)}%".also {
            font.drawString(it, (40f + (easingHP / target.maxHealth) * additionalWidth - font.getStringWidth(it)).coerceAtLeast(40f), 28f - font.fontHeight, Color.WHITE.rgb, false)
        }

        // hp bar
        RenderUtils.drawCircleRect(40f, 28f, 40f + additionalWidth, 33f, 2.5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawCircleRect(40f, 28f, 40f + (easingHP / target.maxHealth) * additionalWidth, 33f, 2.5f, ColorUtils.rainbow().rgb)
        //绘制头像
        //如果target是玩家，将会绘制其头像。
        if (classProvider.isEntityPlayer(target)) {
            playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
        }
        if (playerInfo != null) {

            // Draw head
            val locationSkin = playerInfo.locationSkin

            val renderHurtTime = target.hurtTime - if (target.hurtTime != 0) {
                Minecraft.getMinecraft().timer.renderPartialTicks
            } else {
                0f
            }
            // 受伤的红色效果
            val hurtPercent = renderHurtTime / 10.0F
            GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)

            val scale = if (hurtPercent == 0f) {
                1f
            } else if (hurtPercent < 0.5f) {
                1 - (0.2f * hurtPercent * 2)
            } else {
                0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
            }
            val size = 30

            GL11.glPushMatrix()
            // 受伤的缩放效果
            GL11.glScalef(scale, scale, scale)
            GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)

            mc.textureManager.bindTexture(locationSkin)
            RenderUtils.drawScaledCustomSizeModalRect(
                2, 2, 8F, 8F, 8, 8, size, size,
                64F, 64F
            )

            GL11.glPopMatrix()
        }
    }
    private fun liquidbounce(target: IEntityLivingBase?, width: Float, height: Float) {
        if (target != null) {
            if (easingHealth < 0 || easingHealth > target.maxHealth ||
                abs(easingHealth - target.health) < 0.01
            ) {
                easingHealth = target.health
            }

            // Draw rect box

            RenderUtils.rectangle(0.0, 0.0, width.toDouble(), height.toDouble(), Color(0, 0, 0, 70).rgb)

            var c = Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get())
            var c2 = Color(HUD.redValue2.get(), HUD.greenValue2.get(), HUD.blueValue2.get())
            // Health bar
            //  RenderUtils.drawGradientSideways(0.0, height.toDouble() - 2.0, ((easingHealth / target.maxHealth) * width).toDouble(),
            //    height.toDouble(), c.darker().rgb, c.rgb)
            val startPos = 0.0
            val healthBar = width.toDouble()
            -startPos * 2.0
            RenderUtils.drawGradientSideways(
                startPos,
                height.toDouble() - 2.0,
                startPos + ((easingHealth / target.maxHealth) * (healthBar)).toDouble(),
                height.toDouble(),
                c2.rgb,
                c.rgb
            )

            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

            target.name?.let { fontRenderer.drawStringWithShadow(it, 36, 7, 0xffffff) }

            fontRenderer.drawStringWithShadow(
                "Distance: ${
                    decimalFormat.format(
                        mc.thePlayer!!.getDistanceToEntityBox(
                            target
                        )
                    )
                }", 36, 19, 0xffffff
            )

            // Draw info

            //绘制头像
            //如果target是玩家，将会绘制其头像。
            var playerInfo = mc.netHandler.getPlayerInfo(mc.thePlayer!!.uniqueID)
            if (classProvider.isEntityPlayer(target)) {
                playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            }
            if (playerInfo != null) {

                // Draw head
                val locationSkin = playerInfo.locationSkin

                val renderHurtTime = target.hurtTime - if (target.hurtTime != 0) {
                    Minecraft.getMinecraft().timer.renderPartialTicks
                } else {
                    0f
                }
                // 受伤的红色效果
                val hurtPercent = renderHurtTime / 10.0F
                GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)

                val scale = if (hurtPercent == 0f) {
                    1f
                } else if (hurtPercent < 0.5f) {
                    1 - (0.2f * hurtPercent * 2)
                } else {
                    0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
                }
                val size = 30

                GL11.glPushMatrix()
                // 受伤的缩放效果
                GL11.glScalef(scale, scale, scale)
                GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)

                mc.textureManager.bindTexture(locationSkin)
                RenderUtils.drawScaledCustomSizeModalRect(
                    2, 2, 8F, 8F, 8, 8, size, size,
                    64F, 64F
                )

                GL11.glPopMatrix()
            }
        }
    }

    private fun novoline(target: IEntityLivingBase?, width: Float, height: Float) {
        if (target != null) {
            if (easingHealth < 0 || easingHealth > target.maxHealth ||
                abs(easingHealth - target.health) < 0.01
            ) {
                easingHealth = target.health
            }

            var c = Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get())
            var c2 = Color(HUD.redValue2.get(), HUD.greenValue2.get(), HUD.blueValue2.get())
            // Draw rect box

            //ColorUtils.reAlpha(c.rgb, 0.4F)
            RenderUtils.rectangleBordered(
                0.0, 0.0, width.toDouble(), height.toDouble(), 0.5, Color(0, 0, 0, 30).rgb,
                if (this.outline.get()) RenderUtils.reAlpha(c.rgb, 0.4F) else Color(0, 0, 0, 80).rgb
            )

            RenderUtils.rectangle(36.0, 17.0, width.toDouble() - 4, 27.0, Color(35, 35, 35, 20).rgb)

            RenderUtils.drawGradientSideways(
                36.0, 17.0, 36.0 + ((easingHealth / target.maxHealth) * (width - 40)).toDouble(),
                27.0, c2.rgb, c.rgb
            )

            fontRenderer.drawCenteredString(
                ((target.health / target.maxHealth * 1000.0).toInt() / 10.0F).toString() + "%",
                36.0F + (width - 40) / 2.0F,
                19.0F,
                -1,
                true
            )
            // Health bar

            //RenderUtils.drawGradientSideways(0.0, 34.0, ((easingHealth / target.maxHealth) * width).toDouble(),
            //   36.0, ColorUtils.rainbow().darker().rgb, ColorUtils.rainbow().rgb)

            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

            target.name?.let { fontRenderer.drawStringWithShadow(it, 36, 5, 0xffffff) }

            // Draw info

            //绘制头像
            //如果target是玩家，将会绘制其头像。
            var playerInfo = mc.netHandler.getPlayerInfo(mc.thePlayer!!.uniqueID)
            if (classProvider.isEntityPlayer(target)) {
                playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            }
            if (playerInfo != null) {

                // Draw head
                val locationSkin = playerInfo.locationSkin

                val renderHurtTime = target.hurtTime - if (target.hurtTime != 0) {
                    Minecraft.getMinecraft().timer.renderPartialTicks
                } else {
                    0f
                }
                // 受伤的红色效果
                val hurtPercent = renderHurtTime / 10.0F
                GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)

                val scale = if (hurtPercent == 0f) {
                    1f
                } else if (hurtPercent < 0.5f) {
                    1 - (0.2f * hurtPercent * 2)
                } else {
                    0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
                }
                val size = 30

                GL11.glPushMatrix()
                // 受伤的缩放效果
                GL11.glScalef(scale, scale, scale)
                GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)

                mc.textureManager.bindTexture(locationSkin)
                RenderUtils.drawScaledCustomSizeModalRect(
                    2, 2, 8F, 8F, 8, 8, size, size,
                    64F, 64F
                )

                GL11.glPopMatrix()
            }
        }
    }

    private fun astro(target: IEntityLivingBase, width: Float, height: Float) {
        if (target != null) {
            if (easingHealth < 0 || easingHealth > target.maxHealth ||
                abs(easingHealth - target.health) < 0.01
            ) {
                easingHealth = target.health
            }

            var c = Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get())
            var c2 = Color(HUD.redValue2.get(), HUD.greenValue2.get(), HUD.blueValue2.get())

            // Draw rect box

            //ColorUtils.reAlpha(c.rgb, 0.4F)

            RenderUtils.rectangle(0.0, 0.0, width.toDouble(), height.toDouble(), Color(0, 0, 0, 120).rgb)
            RenderUtils.rectangle(
                2.0, height - 6.0, 2.0 + ((width - 4)).toDouble(),
                height - 2.00, Color(25, 25, 25, 70).rgb
            )

            RenderUtils.drawGradientSideways(
                2.0, height - 6.0, 2.0 + ((easingHealth / target.maxHealth) * (width - 4)).toDouble(),
                height - 2.0, c2.rgb, c.rgb
            )

            // mc.fontRendererObj.drawCenteredString(((target.health * 10).toInt() / 10.0F).toString(), 34.0F + (width - 36) / 2.0F, 14.0F, ColorUtils.getHealthColor(easingHealth, target.maxHealth), true)
            // Health bar

            //RenderUtils.drawGradientSideways(0.0, 34.0, ((easingHealth / target.maxHealth) * width).toDouble(),
            //   36.0, ColorUtils.rainbow().darker().rgb, ColorUtils.rainbow().rgb)

            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

            target.name?.let { fontRenderer.drawStringWithShadow(it, 34, 5, 0xffffff) }

            // Draw info

            //绘制头像
            //如果target是玩家，将会绘制其头像。
            if (classProvider.isEntityPlayer(target)) {
                val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
                if (playerInfo != null) {

                    // Draw head
                    val locationSkin = playerInfo.locationSkin
                    drawHead(locationSkin, 30, 30)
                }
                //如果target不是玩家，将会绘制你自己的头像。
            } else {
                val playerInfo = mc.netHandler.getPlayerInfo(mc.thePlayer!!.uniqueID)
                if (playerInfo != null) {

                    // Draw head
                    val locationSkin = playerInfo.locationSkin
                    drawHead(locationSkin, 30, 30)
                }
            }
        }
    }

    private fun wtf(target: IEntityLivingBase, width: Float, height: Float) {
        if (target != null) {
            if (easingHealth < 0 || easingHealth > target.maxHealth ||
                abs(easingHealth - target.health) < 0.01
            ) {
                easingHealth = target.health
            }

            var c = Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get())
            var c2 = Color(HUD.redValue2.get(), HUD.greenValue2.get(), HUD.blueValue2.get())

            // Draw rect box

            //ColorUtils.reAlpha(c.rgb, 0.4F)
            RenderUtils.rectangleBordered(
                0.0,
                0.0,
                width.toDouble(),
                height.toDouble(),
                1.0,
                Color(95, 95, 95, 255).rgb,
                Color(0, 0, 0, 255).rgb
            )
            RenderUtils.rectangleBordered(
                1.5,
                1.5,
                width.toDouble() - 1.5,
                height.toDouble() - 1.5,
                1.0,
                Color(45, 45, 45, 255).rgb,
                Color(65, 65, 65, 255).rgb
            )
            //RenderUtils.rectangleBordered(0.0, 0.0, width.toDouble(), height.toDouble(), 0.5, Color(0,0,0,60).rgb, Color(0,0,0,90).rgb)

            val startPos = 6.0
            val barWidth = width.toDouble() - startPos * 2.0
            RenderUtils.rectangle(
                startPos - 0.5, 15.5, (startPos + (barWidth)) + 0.5,
                26.5, Color(25, 25, 25, 255).rgb
            )

            RenderUtils.drawGradientSideways(
                startPos, 16.0, startPos + ((easingHealth / target.maxHealth) * barWidth),
                26.0, c2.rgb, c.rgb
            )

            Fonts.font35.drawCenteredString(
                ((target.health * 10).toInt() / 10.0F).toString() + " HP",
                width / 2F,
                19.0F,
                -1,
                true
            )
            // Health bar

            //RenderUtils.drawGradientSideways(0.0, 34.0, ((easingHealth / target.maxHealth) * width).toDouble(),
            //   36.0, ColorUtils.rainbow().darker().rgb, ColorUtils.rainbow().rgb)

            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

            target.name?.let { fontRenderer.drawStringWithShadow(it, startPos.toInt() + 2, 6, 0xffffff) }

            fontRenderer.drawStringWithShadow(
                "Distance: " + mc.thePlayer!!.getDistanceToEntity(target).toInt() + "m",
                startPos.toInt() + 2,
                30,
                0xffffff
            )
            // Draw info
        }
    }

    private fun nameless(target: IEntityLivingBase, width: Float, height: Float) {
        if (target != null) {
            if (easingHealth < 0 || easingHealth > target.maxHealth ||
                abs(easingHealth - target.health) < 0.01
            ) {
                easingHealth = target.health
            }

            // Draw rect box
            RenderUtils.drawRoundedRect(0.0f, 0.0f, width, height, 2.4F.toInt(), Color(0, 0, 0, 100).rgb)
            // Draw rect box
            var c = Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get())
            var c2 = Color(HUD.redValue2.get(), HUD.greenValue2.get(), HUD.blueValue2.get())
            // Health bar
            //  RenderUtils.drawGradientSideways(0.0, height.toDouble() - 2.0, ((easingHealth / target.maxHealth) * width).toDouble(),
            //    height.toDouble(), c.darker().rgb, c.rgb)
            val startPos = 2.0
            val barWidth = width.toDouble() - startPos * 2.0

            //  RenderUtils.rectangle(startPos, height.toDouble() - 3.0, startPos + ((target.totalArmorValue / 20) * (barWidth)),
            //    height.toDouble() - 2.0, Color(55, 105, 255).rgb)


            RenderUtils.drawGradientSideways(
                startPos, height.toDouble() - 3.0, startPos + ((easingHealth / target.maxHealth) * (barWidth)),
                height.toDouble() - 2.0, c2.rgb, c.rgb
            )

            easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime

            target.name?.let { fontRenderer.drawStringWithShadow(it, 34, 7, 0xffffff) }

            fontRenderer.drawStringWithShadow(
                "Distance: ${
                    decimalFormat.format(
                        mc.thePlayer!!.getDistanceToEntityBox(
                            target
                        )
                    )
                }", 34, 19, 0xffffff
            )

            // Draw info

            //绘制头像
            //如果target是玩家，将会绘制其头像。
            var playerInfo = mc.netHandler.getPlayerInfo(mc.thePlayer!!.uniqueID)
            if (classProvider.isEntityPlayer(target)) {
                playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
            }
            if (playerInfo != null) {

                // Draw head
                val locationSkin = playerInfo.locationSkin

                val renderHurtTime = target.hurtTime - if (target.hurtTime != 0) {
                    Minecraft.getMinecraft().timer.renderPartialTicks
                } else {
                    0f
                }
                // 受伤的红色效果
                val hurtPercent = renderHurtTime / 10.0F
                GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)

                val scale = if (hurtPercent == 0f) {
                    1f
                } else if (hurtPercent < 0.5f) {
                    1 - (0.2f * hurtPercent * 2)
                } else {
                    0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
                }
                val size = 30

                GL11.glPushMatrix()
                // 受伤的缩放效果
                GL11.glScalef(scale, scale, scale)
                GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)

                mc.textureManager.bindTexture(locationSkin)
                RenderUtils.drawScaledCustomSizeModalRect(
                    2, 2, 8F, 8F, 8, 8, size, size,
                    64F, 64F
                )

                GL11.glPopMatrix()
            }
        }
    }


    private fun drawHead(skin: IResourceLocation, width: Int, height: Int) {

        mc.textureManager.bindTexture(skin)
        RenderUtils.drawScaledCustomSizeModalRect(
            2, 2, 8F, 8F, 8, 8, width, height,
            64F, 64F
        )
    }
}