/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/liquidbounce/
 */
package dragline.ui.client.hud.element.elements

import dragline.DragLine
import dragline.api.minecraft.client.gui.IFontRenderer
import dragline.features.module.modules.client.HUD
import dragline.ui.client.hud.element.Border
import dragline.ui.client.hud.element.Element
import dragline.ui.client.hud.element.ElementInfo
import dragline.ui.client.hud.element.Side
import dragline.ui.client.hud.element.elements.FadeState.*
import dragline.ui.font.Fonts
import dragline.utils.ColorUtil
import dragline.utils.EaseUtils
import dragline.utils.MinecraftInstance
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.IntegerValue
import dragline.value.ListValue
import me.nelly.utils.RoundedUtil
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock
import java.awt.Color
import kotlin.math.max


/**
 * CustomHUD Notification element
 */
@Native
@RegisterLock
@ElementInfo(name = "Notifications")
class Notifications(
    x: Double = 0.0,
    y: Double = 0.0,
    scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {


    private val titleShadow = BoolValue("TitleShadow", false)
    private val contentShadow = BoolValue("ContentShadow", true)
    private val whiteText = BoolValue("WhiteTextColor", true)

    companion object {
        val styleValue = ListValue("Mode", arrayOf("DragLine","Tenacity","Narlebone","Noteless","Pride"), "DragLine")
        val redValue = IntegerValue("Red", 255, 0, 255)
        val greenValue = IntegerValue("Green", 255, 0, 255)
        val blueValue = IntegerValue("Blue", 255, 0, 255)
    }

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Notification", "This is an example notification.", NotifyType.INFO)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        // bypass java.util.ConcurrentModificationException
        DragLine.hud.notifications.map { it }.forEachIndexed { index, notify ->
            GL11.glPushMatrix()

            if (notify.drawNotification(
                    index,
                    Fonts.font35,
                    contentShadow.get(),
                    titleShadow.get(),
                    whiteText.get()
                )
            ) {
                DragLine.hud.notifications.remove(notify)
            }

            GL11.glPopMatrix()
        }

        if (DragLine.wrapper.classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (!DragLine.hud.notifications.contains(exampleNotification)) {
                DragLine.hud.addNotification(exampleNotification)
            }

            exampleNotification.fadeState = STAY
            exampleNotification.displayTime = System.currentTimeMillis()

            return Border(-exampleNotification.width.toFloat(), -exampleNotification.height.toFloat(), 0F, 0F)
        }

        return null
    }
}


class Notification(
    val title: String,
    val content: String,
    val type: NotifyType,
    val time: Int = 1500,
    private val animeTime: Int = 500
) {
    var width = 100
    val height = 30

    var x = 0F

    var fadeState = IN
    private var nowY = -height
    var displayTime = System.currentTimeMillis()
    private var animeXTime = System.currentTimeMillis()
    private var animeYTime = System.currentTimeMillis()

    /**
     * Draw notification
     */
    fun drawNotification(
        index: Int, font: IFontRenderer,
        contentShadow: Boolean,
        titleShadow: Boolean,
        whiteText: Boolean
    ): Boolean {
        this.width = 100.coerceAtLeast(
            font.getStringWidth(content)
                .coerceAtLeast(font.getStringWidth(title)) + 15
        )
        val realY = -(index + 1) * height
        val nowTime = System.currentTimeMillis()
        var transY = nowY.toDouble()
        font.getStringWidth("$title: $content")
        val s: String? = when (type) {
            NotifyType.SUCCESS -> "SUCCESS"
            NotifyType.ERROR -> "ERROR"
            NotifyType.WARNING -> "WARNING"
            NotifyType.INFO -> "INFO"
        }

        val textColor: Int = if (whiteText) {
            Color(255, 255, 255).rgb
        } else {
            Color(10, 10, 10).rgb
        }
        val gradientColor1 = Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get(), HUD.a.get())
        val gradientColor2 = Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get(), HUD.a.get())
        val gradientColor3 = Color(HUD.redValue2.get(), HUD.greenValue2.get(), HUD.blueValue2.get(), HUD.a2.get())
        val gradientColor4 = Color(HUD.redValue2.get(), HUD.greenValue2.get(), HUD.blueValue2.get(), HUD.a2.get())

        // Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutExpo(pct)
            }
            transY += (realY - nowY) * pct
        } else {
            animeYTime = nowTime
        }

        // X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            IN -> {
                if (pct > 1) {
                    fadeState = STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutExpo(pct)
            }

            STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = OUT
                    animeXTime = nowTime
                }
            }

            OUT -> {
                if (pct > 1) {
                    fadeState = END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - EaseUtils.easeInExpo(pct)
            }

            END -> {
                return true
            }
        }
        val transX = width - (width * pct) - width
        GL11.glTranslated(transX, transY, 0.0)
        // draw notify
        val style = Notifications.styleValue.get()

        if (style == "Pride") {
            val font = Fonts.font35
            -(index + 1) * height

            RenderUtils.drawShadow(-22F, 0F, width + 22F, height.toFloat(), 200F)
            RenderUtils.drawRect(
                -22.0f,
                0.0f,
                max(width - width * ((System.currentTimeMillis() - displayTime) / (animeTime * 2F + time)), -22.0F),
                height.toFloat(),
                Color(255, 255, 255, 60)
            )
            font.drawString(title, 7F, 4F, -1, true)
            font.drawString(content, 7F, 17F, -1, true)
            RenderUtils.drawImage(
                MinecraftInstance.classProvider.createResourceLocation("liquidbounce/notifications/pride/" + type.name + ".png"),
                -19,
                3,
                22,
                22
            )
        }
        if (style.contains("Narlebone")) {
            RenderUtils.drawGradientRound(
                44F,
                -2F,
                max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), 0F),
                28F,
                HUD.ra.get(),
                ColorUtil.applyOpacity(gradientColor4, .85f),
                gradientColor1,
                gradientColor3,
                gradientColor2
            )

            if (s == "INFO") {
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), 0F),
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    width.toFloat(),
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                Fonts.ico1.drawString("h", 48F, 3F, Color(224, 194, 30, 255).rgb)
                Fonts.font35.drawString(title, 62f, 3f, Color(224, 194, 30, 255).rgb)
                Fonts.font35.drawString(content, 48f, 16f, Color.white.rgb)
            }
            if (s == "WARNING") {
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    width.toFloat(),
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                Fonts.ico1.drawString("h", 48F, 3F, Color(224, 194, 30, 255).rgb)
                Fonts.font35.drawString(title, 62f, 3f, Color(224, 194, 30, 255).rgb)
                Fonts.font35.drawString(content, 48f, 16f, Color.white.rgb)
            }
            if (s == "SUCCESS") {
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    width - 20F,
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                //  RenderUtils.drawRound(44F,-2F,width - 20F,28F,4.5F, Color(23 ,23, 23,100))
                Fonts.ico1.drawString("i", 48F, 3F, Color(0, 157, 255, 240).rgb)
                Fonts.font40.drawString(title, 62f, 3f, Color(0, 157, 255, 240).rgb)
                Fonts.font35.drawString(content, 48f, 16f, Color.white.rgb)
            }
            if (s == "ERROR") {
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    width - 20F,
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                // RenderUtils.drawRound(44F,-2F,width - 20F,28F,4.5F, Color(23 ,23, 23,100))
                Fonts.ico1.drawString("g", 48F, 3F, Color(206, 33, 33, 240).rgb)
                Fonts.font40.drawString(title, 62F, 3f, Color(206, 33, 33, 240).rgb)
                Fonts.font35.drawString(content, 48f, 16f, Color.white.rgb)
            }
            return false
        }
        if (style.equals("Noteless")) {
            RenderUtils.drawShadow(-22F, 0F, width.toFloat() + 22, height.toFloat(), 255F)
            RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), type.renderColor)
            RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), Color(0, 0, 0, 150))
            RenderUtils.drawRect(
                -22F,
                height - 2F,
                max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F),
                height.toFloat(),
                type.renderColor
            )

            RenderUtils.drawImage(
                MinecraftInstance.classProvider.createResourceLocation("liquidbounce/notifications/noteless/" + type.name + ".png"),
                -19,
                3,
                22,
                22
            )

            Fonts.font35.drawString(title, 7F, 4F, -1)
            Fonts.font30.drawString(content, 7F, 17F, -1)
            GlStateManager.resetColor()
            return false
        }
        if (style.equals("DragLine")) {

            var colorRed: Int = 0
            var colorGreen: Int = 0
            var colorBlue: Int = 0

            if (s == "SUCCESS") {
                //success
                colorRed = 36
                colorGreen = 211
                colorBlue = 99
            }

            if (s == "ERROR") {
                //error
                colorRed = 248
                colorGreen = 72
                colorBlue = 72
            }

            if (s == "WARNING") {

                //warning
                colorRed = 251
                colorGreen = 189
                colorBlue = 23
            }

            //info
            if (s == "INFO") {
                colorRed = 242
                colorGreen = 242
                colorBlue = 242
            }


            RenderUtils.drawRoundedCornerRect(0F + 3f, 0F, width.toFloat() + 5f, 27f - 5f, 2f, Color(0,0,0,120).rgb)
            RenderUtils.drawRoundedCornerRect(
                0F + 3f,
                0F,
                max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)) + 5f, 0F),
                27f - 5f,
                2f,
                Color(colorRed, colorGreen, colorBlue, 150).rgb
            )
            Fonts.font35.drawString(title, 6F, 3F, textColor, titleShadow)
            font.drawString(content, 6F, 12F, textColor, contentShadow)
            return false
        }


        if (style.equals("Tenacity")) {
            val fontRenderer = Fonts.font35
            val thisWidth = 100.coerceAtLeast(
                fontRenderer.getStringWidth(this.title).coerceAtLeast(fontRenderer.getStringWidth(this.content)) + 40
            )
            val error =
                MinecraftInstance.classProvider.createResourceLocation("liquidbounce/notifications/tenacity/error.png")
            val successful =
                MinecraftInstance.classProvider.createResourceLocation("liquidbounce/notifications/tenacity/success.png")
            val warn =
                MinecraftInstance.classProvider.createResourceLocation("liquidbounce/notifications/tenacity/warning.png")
            val info =
                MinecraftInstance.classProvider.createResourceLocation("liquidbounce/notifications/tenacity/info.png")
            if (type.renderColor == Color(0xFF2F2F)) {
                RoundedUtil.drawRound(
                    -18F,
                    1F,
                    thisWidth.toFloat(),
                    height.toFloat() - 2F,
                    5f,
                    true,
                    Color(180, 0, 0, 190)
                )
                RenderUtils.drawImage(error, -13, 5, 18, 18)
                Fonts.font35.drawString(title, 9F, 16F, Color(255, 255, 255, 255).rgb)
                Fonts.font40.drawString(content, 9F, 6F, Color(255, 255, 255, 255).rgb)
            } else if (type.renderColor == Color(0x60E092)) {
                RoundedUtil.drawRound(
                    -16F,
                    1F,
                    thisWidth.toFloat(),
                    height.toFloat() - 2F,
                    5f,
                    true,
                    Color(0, 180, 0, 190)
                )
                RenderUtils.drawShadow(
                    -16F,
                    1F,
                    thisWidth.toFloat(),
                    height.toFloat() - 2F,
                    255f
                )
                RenderUtils.drawImage(successful, -13, 5, 18, 18)
                Fonts.font35.drawString(title, 9F, 16F, Color(255, 255, 255, 255).rgb)
                Fonts.font40.drawString(content, 9F, 6F, Color(255, 255, 255, 255).rgb)
            } else if (type.renderColor == Color(0xF5FD00)) {
                RoundedUtil.drawRound(-16F,
                    1F,
                    thisWidth.toFloat(),
                    height.toFloat() - 2F,
                    5f,
                    true,
                    Color(0, 0, 0, 190)
                )
                RenderUtils.drawImage(warn, -13, 5, 18, 18)
                Fonts.font35.drawString(title, 9F, 16F, Color(255, 255, 255, 255).rgb)
                Fonts.font40.drawString(content, 9F, 6F, Color(255, 255, 255, 255).rgb)
            } else {
                RoundedUtil.drawRound(
                    -16F,
                    1F,
                    thisWidth.toFloat(),
                    height.toFloat() - 2F,
                    5f,
                    true,
                    Color(0, 0, 0, 190)
                )
                RenderUtils.drawImage(info, -13, 5, 18, 18)
                Fonts.font35.drawString(title, 9F, 16F, Color(255, 255, 255, 255).rgb)
                Fonts.font40.drawString(content, 9F, 6F, Color(255, 255, 255, 255).rgb)
            }
            return false
        }
        if (style.equals("Narlebone")) {
            RenderUtils.drawGradientRound(
                44F,
                -2F,
                max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), 0F),
                28F,
                HUD.ra.get(),
                ColorUtil.applyOpacity(gradientColor4, .85f),
                gradientColor1,
                gradientColor3,
                gradientColor2
            )

            if (s == "INFO") {
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), 0F),
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    width.toFloat(),
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                Fonts.ico1.drawString("h", 48F, 3F, Color(224, 194, 30, 255).rgb)
                Fonts.font35.drawString(title, 62f, 3f, Color(224, 194, 30, 255).rgb)
                Fonts.font35.drawString(content, 48f, 16f, Color.white.rgb)
            }
            if (s == "WARNING") {
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    width.toFloat(),
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                Fonts.ico1.drawString("h", 48F, 3F, Color(224, 194, 30, 255).rgb)
                Fonts.font35.drawString(title, 62f, 3f, Color(224, 194, 30, 255).rgb)
                Fonts.font35.drawString(content, 48f, 16f, Color.white.rgb)
            }
            if (s == "SUCCESS") {
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    width - 20F,
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                //  RenderUtils.drawRound(44F,-2F,width - 20F,28F,4.5F, Color(23 ,23, 23,100))
                Fonts.ico1.drawString("i", 48F, 3F, Color(0, 157, 255, 240).rgb)
                Fonts.font40.drawString(title, 62f, 3f, Color(0, 157, 255, 240).rgb)
                Fonts.font35.drawString(content, 48f, 16f, Color.white.rgb)
            }
            if (s == "ERROR") {
                RenderUtils.drawGradientRound(
                    44F,
                    -2F,
                    width - 20F,
                    28F,
                    HUD.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )
                // RenderUtils.drawRound(44F,-2F,width - 20F,28F,4.5F, Color(23 ,23, 23,100))
                Fonts.ico1.drawString("g", 48F, 3F, Color(206, 33, 33, 240).rgb)
                Fonts.font40.drawString(title, 62F, 3f, Color(206, 33, 33, 240).rgb)
                Fonts.font35.drawString(content, 48f, 16f, Color.white.rgb)
            }
            return false
        }
        return false
    }
}



