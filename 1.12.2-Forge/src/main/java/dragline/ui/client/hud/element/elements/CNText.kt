package dragline.ui.client.hud.element.elements

import dragline.DragLine
import dragline.features.module.modules.client.HUD
import dragline.ui.client.hud.element.Border
import dragline.ui.client.hud.element.Element
import dragline.ui.client.hud.element.ElementInfo
import dragline.ui.client.hud.element.Side
import dragline.ui.cnfont.FontLoaders
import dragline.utils.CPSCounter
import dragline.utils.EntityUtils
import dragline.utils.ServerUtils
import dragline.value.*
import dragline.utils.render.Palette
import dragline.utils.render.RenderUtils
import dragline.utils.render.shader.shaders.RainbowFontShader
import dragline.utils.timer.MSTimer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ChatAllowedCharacters
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "CNText")
class CNText(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F,
             side: Side = Side.default()) : Element(x, y, scale, side) {
    companion object {

        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")
        val DECIMAL_FORMAT = DecimalFormat("0.00")
        val DECIMAL_FORMAT_INT = DecimalFormat("0")

    }

    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom", "Rainbow", "Fade", "Astolfo", "NewRainbow","Gident"), "Custom")
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val displayString = TextValue("DisplayText", "测试")
    private val redValue = IntegerValue("Text-R", 255, 0, 255)
    private val greenValue = IntegerValue("Text-G", 255, 0, 255)
    private val blueValue = IntegerValue("Text-B", 255, 0, 255)
    private val colorRedValue2 = IntegerValue("Text-R2", 0, 0, 255)
    private val colorGreenValue2 = IntegerValue("Text-G2", 111, 0, 255)
    private val colorBlueValue2 = IntegerValue("Text-B2", 255, 0, 255)
    private val shadowValue = ListValue("Top-Shadow", arrayOf("None", "Basic", "Thick"), "Thick")
    private val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
    private val newRainbowIndex = IntegerValue("NewRainbowOffset", 1, 1, 50)
    private val astolfoRainbowOffset = IntegerValue("AstolfoOffset", 5, 1, 20)
    private val astolfoclient = IntegerValue("AstolfoRange", 109, 1, 765)
    private val astolfoRainbowIndex = IntegerValue("AstolfoIndex", 109, 1, 300)
    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val Mode = ListValue("Border-Mode", arrayOf("Slide", "Skeet","Top", "Onetap"), "Top")
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val shadow = BoolValue("Shadow", true)
    private val bord = BoolValue("Border", true)
    private val slide = BoolValue("Slide", false)
    private val char = BoolValue("NotChar", false)
    private val slidedelay = IntegerValue("SlideDelay", 100, 0, 1000)
    private val balpha = IntegerValue("BordAlpha", 255, 0, 255)
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val amountValue = IntegerValue("Amount", 25, 1, 50)
    private var fontValue = FontLoaders.CUSTOM18

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var speedStr = ""
    private var displayText: String = ""

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode)
                "DragLine | Fps:%fps% | %serverip%"
            else
                displayString.get()


            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        if (mc.thePlayer != null) {
            when (str) {
                "x" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.posX)
                "y" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.posY)
                "z" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.posZ)
                "xInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.posX)
                "yInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.posY)
                "zInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!!!.posZ)
                "xdp" -> return mc.thePlayer!!.posX.toString()
                "ydp" -> return mc.thePlayer!!.posY.toString()
                "zdp" -> return mc.thePlayer!!.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(mc.thePlayer!!.motionX * mc.thePlayer!!.motionX + mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ))
                "ping" -> return EntityUtils.getPing(mc.thePlayer!!).toString()
                "health" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.health)
                "maxHealth" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.maxHealth)
                "healthInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.health)
                "maxHealthInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.maxHealth)
                "yaw" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.rotationYaw)
                "pitch" -> return DECIMAL_FORMAT.format(mc.thePlayer!!.rotationPitch)
                "yawInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.rotationYaw)
                "pitchInt" -> return DECIMAL_FORMAT_INT.format(mc.thePlayer!!.rotationPitch)
                "bps" -> return speedStr
                "hurtTime" -> return mc.thePlayer!!.hurtTime.toString()
                "onGround" -> return mc.thePlayer!!.onGround.toString()
            }
        }

        return when (str) {
            "username" -> mc2.getSession().username
            "clientname" -> DragLine.CLIENT_NAME
            "clientversion" -> "b${DragLine.CLIENT_VERSION}"
            "clientcreator" -> DragLine.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverip" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
            "userName" -> mc2.session.username
            "clientName" -> DragLine.CLIENT_NAME
            "clientVersion" -> DragLine.CLIENT_VERSION.toString()
            "clientCreator" -> DragLine.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverIp" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
//            "watchdogLastMin" -> BanChecker.WATCHDOG_BAN_LAST_MIN.toString()
//            "staffLastMin" -> BanChecker.STAFF_BAN_LAST_MIN.toString()
//            "sessionTime" -> return SessionUtils.getFormatSessionTime()
//            "worldTime" -> return SessionUtils.getFormatWorldTime()
            else -> null // Null = don't replace
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

    /**
     * Draw element
     */
    var slidetext: Int = 0
    var slidetimer: MSTimer = MSTimer()
    var doslide = true
    override fun drawElement(): Border {
        val fontRenderer = fontValue
        var length2 = 4.5f
        val charArray = displayText.toCharArray()
        if(char.get()) {
            length2 = fontRenderer.getStringWidth(displayText).toFloat()
        } else {
            for (charIndex in charArray) {
                length2 += fontRenderer.getStringWidth(charIndex.toString())
            }
        }
        if (slide.get() && !classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (slidetimer.hasTimePassed(slidedelay.get().toLong())) {
                if (slidetext <= display.length && doslide) {
                    slidetext += 1
                    slidetimer.reset()
                } else {
                    if (!doslide && slidetext >= 0) {
                        slidetext -= 1
                        slidetimer.reset()
                    }
                }
            }
            if (slidetext == display.length && doslide) {
                doslide = false
            } else {
                if (slidetext == 0 && !doslide) {
                    doslide = true
                }
            }
            displayText = display.substring(0, slidetext)
        } else {
            displayText = display
        }
        val mwc1 = Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get()).rgb
        val mwc2 = Color(HUD.redValue2.get(), HUD.greenValue2.get(), HUD.blueValue2.get()).rgb
        if (this.Mode.get().equals("Top")) {
            when (shadowValue.get()) {
                "Basic" ->   RenderUtils.drawShadow(-2.5f, -2.5f, (fontRenderer.getStringWidth(displayText) + 2).toFloat(), fontRenderer.height.toFloat(),200f)
                "Thick" -> {
                    RenderUtils.drawShadow(-2f, -2f, (fontRenderer.getStringWidth(displayText) + 2).toFloat(), fontRenderer.height.toFloat(),200f)
                    RenderUtils.drawShadow(-2f, -2f, (fontRenderer.getStringWidth(displayText) + 2).toFloat(), fontRenderer.height.toFloat(),200f)
                }
            }

            RenderUtils.drawRound(-2f, -2f, (fontRenderer.getStringWidth(displayText) + 2).toFloat(), fontRenderer.height.toFloat(), 0F, Color(0,0,0,100))
            RenderUtils.drawGradientSideways(-3.0,-3.0, (fontRenderer.getStringWidth(displayText) + 2).toDouble(), -2.0, mwc1, mwc2)
        }

        if (this.Mode.get().equals("Onetap")) {
            Gui.drawRect(-2,-5, fontRenderer.getStringWidth(displayText) + 2, fontRenderer.height, Color(0,0,0,90).rgb)
            RenderUtils.drawGradientSideways(-1.0,-2.0,
                (fontRenderer.getStringWidth(displayText) + 1).toDouble(), -4.0, mwc1, mwc2)
        }
        val colorMode = colorModeValue.get()
        val color = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb
        val rainbow = colorMode.equals("Rainbow", ignoreCase = true)
        if (bord.get()) {
            if (Mode.get() == "Skeet") {
                RenderUtils.autoExhibition(-4.0, -5.2, (length2).toDouble(), (fontRenderer.height + 1.5).toDouble(),1.0)
                val barLength = (length2).toDouble()
                for (i in 0..(amountValue.get()-1)) {
                    val barStart = i.toDouble() / amountValue.get().toDouble() * barLength
                    val barEnd = (i + 1).toDouble() / amountValue.get().toDouble() * barLength
                    RenderUtils.drawGradientSideways(-1.4 + barStart, -2.7, -1.4 + barEnd, -2.0,
                        when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), i * distanceValue.get(), displayText.length * 200).rgb
                            colorMode.equals("Astolfo", ignoreCase = true) -> RenderUtils.Astolfo(i * distanceValue.get(), saturationValue.get(), brightnessValue.get(), astolfoRainbowOffset.get(), astolfoRainbowIndex.get(), astolfoclient.get().toFloat())
                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(),1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb
                            colorMode.equals("NewRainbow", ignoreCase = true) -> RenderUtils.getRainbow(i * distanceValue.get(),newRainbowIndex.get(), saturationValue.get(), brightnessValue.get())
                            else -> color
                        },
                        when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), i * distanceValue.get(), displayText.length * 200).rgb
                            colorMode.equals("Astolfo", ignoreCase = true) -> RenderUtils.Astolfo(i * distanceValue.get(), saturationValue.get(), brightnessValue.get(), astolfoRainbowOffset.get(), astolfoRainbowIndex.get(), astolfoclient.get().toFloat())
                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(),1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb
                            colorMode.equals("NewRainbow", ignoreCase = true) -> RenderUtils.getRainbow(i * distanceValue.get(),newRainbowIndex.get(), saturationValue.get(), brightnessValue.get())
                            else -> color
                        })
                }
            }
            if (Mode.get() == "Slide") {
                RenderUtils.drawRect(-4.0f, -4.5f, (length2).toFloat(), fontRenderer.height.toFloat(), Color(0, 0, 0, balpha.get()).rgb)
                val barLength = (length2 + 1).toDouble()
                for (i in 0..(amountValue.get()-1)) {
                    val barStart = i.toDouble() / amountValue.get().toDouble() * barLength
                    val barEnd = (i + 1).toDouble() / amountValue.get().toDouble() * barLength
                    RenderUtils.drawGradientSideways(-4.0 + barStart, -4.2, -1.0 + barEnd, -3.0,
                        when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), i * distanceValue.get(), displayText.length * 200).rgb
                            colorMode.equals("Astolfo", ignoreCase = true) -> RenderUtils.Astolfo(i * distanceValue.get(), saturationValue.get(), brightnessValue.get(), astolfoRainbowOffset.get(), astolfoRainbowIndex.get(), astolfoclient.get().toFloat())
                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(),1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb
                            colorMode.equals("NewRainbow", ignoreCase = true) -> RenderUtils.getRainbow(i * distanceValue.get(),newRainbowIndex.get(), saturationValue.get(), brightnessValue.get())
                            else -> color
                        },
                        when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), i * distanceValue.get(), displayText.length * 200).rgb
                            colorMode.equals("Astolfo", ignoreCase = true) -> RenderUtils.Astolfo(i * distanceValue.get(), saturationValue.get(), brightnessValue.get(), astolfoRainbowOffset.get(), astolfoRainbowIndex.get(), astolfoclient.get().toFloat())
                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(),1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + i * distanceValue.get()) / 10)).rgb
                            colorMode.equals("NewRainbow", ignoreCase = true) -> RenderUtils.getRainbow(i * distanceValue.get(),newRainbowIndex.get(), saturationValue.get(), brightnessValue.get())
                            else -> color
                        })
                }
            }
        }
        val counter = intArrayOf(0)
        if(char.get()){
            val rainbow = colorMode.equals("Rainbow", ignoreCase = true)
            RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                fontRenderer.drawString(displayText, 0F, 0F, when {
                    rainbow -> 0
                    colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), counter[0] * 100, displayText.length * 200).rgb
                    colorMode.equals("Astolfo", ignoreCase = true) -> RenderUtils.Astolfo(counter[0] * 100, saturationValue.get(), brightnessValue.get(), astolfoRainbowOffset.get(), astolfoRainbowIndex.get(), astolfoclient.get().toFloat())
                    colorMode.equals("NewRainbow", ignoreCase = true) -> RenderUtils.getRainbow(counter[0] * 100, newRainbowIndex.get(), saturationValue.get(), brightnessValue.get())
                    colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + counter[0]) / 10)).rgb
                    else -> color
                }, shadow.get())
                if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen) && editTicks <= 40)
                    fontRenderer.drawString("_", fontRenderer.getStringWidth(displayText).toFloat(),
                        0F, when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), counter[0] * 100, displayText.length * 200).rgb
                            colorMode.equals("Astolfo", ignoreCase = true) -> RenderUtils.Astolfo(counter[0] * 100, saturationValue.get(), brightnessValue.get(), astolfoRainbowOffset.get(), astolfoRainbowIndex.get(), astolfoclient.get().toFloat())
                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + counter[0]) / 10)).rgb
                            colorMode.equals("NewRainbow", ignoreCase = true) -> RenderUtils.getRainbow(counter[0] * 100, newRainbowIndex.get(), saturationValue.get(), brightnessValue.get())
                            else -> color
                        }, shadow.get())
                counter[0] += 1
            }
        } else {
            var length = 0
            RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                for (charIndex in charArray) {
                    val rainbow = colorMode.equals("Rainbow", ignoreCase = true)
                    fontRenderer.drawString(charIndex.toString(), length.toFloat(), 0F, when {
                        rainbow -> 0
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), counter[0] * 100, displayText.length * 200).rgb
                        colorMode.equals("Astolfo", ignoreCase = true) -> RenderUtils.Astolfo(counter[0] * 100, saturationValue.get(), brightnessValue.get(), astolfoRainbowOffset.get(), astolfoRainbowIndex.get(), astolfoclient.get().toFloat())
                        colorMode.equals("NewRainbow", ignoreCase = true) -> RenderUtils.getRainbow(counter[0] * 100, newRainbowIndex.get(), saturationValue.get(), brightnessValue.get())
                        colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + counter[0]) / 10)).rgb
                        else -> color
                    }, shadow.get())
                    counter[0] += 1
                    counter[0] = counter[0].coerceIn(0, displayText.length)
                    length += fontRenderer.getStringWidth(charIndex.toString())
                }
                if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen) && editTicks <= 40)
                    fontRenderer.drawString("_", length2,
                        0F, when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), counter[0] * 100, displayText.length * 200).rgb
                            colorMode.equals("Astolfo", ignoreCase = true) -> RenderUtils.Astolfo(counter[0] * 100, saturationValue.get(), brightnessValue.get(), astolfoRainbowOffset.get(), astolfoRainbowIndex.get(), astolfoclient.get().toFloat())
                            colorMode.equals("Gident", ignoreCase = true) -> RenderUtils.getGradientOffset(Color(redValue.get(), greenValue.get(), blueValue.get()), Color(colorRedValue2.get(), colorGreenValue2.get(), colorBlueValue2.get(), 1), (Math.abs(System.currentTimeMillis() / gidentspeed.get().toDouble() + counter[0]) / 10)).rgb
                            colorMode.equals("NewRainbow", ignoreCase = true) -> RenderUtils.getRainbow(counter[0] * 100, newRainbowIndex.get(), saturationValue.get(), brightnessValue.get())
                            else -> color
                        }, shadow.get())
            }
        }

        if (editMode && !classProvider.isGuiHudDesigner(mc.currentScreen)) {
            editMode = false
            updateElement()
        }
        return Border(
            -2F,
            -2F,
            length2,
            fontRenderer.height.toFloat()
        )
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        displayText = if (editMode) displayString.get() else display
    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L)
                editMode = true

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && classProvider.isGuiHudDesigner(mc.currentScreen)) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty())
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))

                updateElement()
                return
            }

            if (ChatAllowedCharacters.isAllowedCharacter(c) || c == '§')
                displayString.set(displayString.get() + c)

            updateElement()
        }
    }

    fun setColor(c: Color): CNText {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }


    fun drawRect(x: Float, y: Float, x2: Float, y2: Float, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y.toDouble())
        GL11.glVertex2d(x.toDouble(), y2.toDouble())
        GL11.glVertex2d(x2.toDouble(), y2.toDouble())
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun drawRect(x: Double, y: Double, x2: Double, y2: Double, color: Int) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        glColor(color)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glVertex2d(x2, y)
        GL11.glVertex2d(x, y)
        GL11.glVertex2d(x, y2)
        GL11.glVertex2d(x2, y2)
        GL11.glEnd()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
    }

    fun glColor(red: Int, green: Int, blue: Int, alpha: Int) {
        GlStateManager.color(red / 255f, green / 255f, blue / 255f, alpha / 255f)
    }

    fun glColor(color: Color) {
        val red = color.red / 255f
        val green = color.green / 255f
        val blue = color.blue / 255f
        val alpha = color.alpha / 255f
        GlStateManager.color(red, green, blue, alpha)
    }

    fun glColor(hex: Int) {
        val alpha = (hex shr 24 and 0xFF) / 255f
        val red = (hex shr 16 and 0xFF) / 255f
        val green = (hex shr 8 and 0xFF) / 255f
        val blue = (hex and 0xFF) / 255f
        GlStateManager.color(red, green, blue, alpha)
    }

}