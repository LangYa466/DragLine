package me.paimon.newVer.element.module.value.impl

import me.paimon.newVer.ColorManager
import me.paimon.newVer.MouseUtils
import me.paimon.newVer.element.components.Slider
import me.paimon.newVer.element.module.value.ValueElement
import dragline.ui.font.Fonts
import dragline.utils.render.RenderUtils
import dragline.value.FloatValue
import java.awt.Color
import java.math.BigDecimal

class FloatElement(val savedValue: FloatValue): ValueElement<Float>(savedValue) {
    private val slider = Slider()
    private var dragged = false

    override fun drawElement(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float, bgColor: Color, accentColor: Color): Float {
        val valueDisplay = 30F + Fonts.font40.getStringWidth("${savedValue.maximum.toInt().toFloat() + 0.01F}")
        val maxLength = Fonts.font40.getStringWidth("${savedValue.maximum}")
        val minLength = Fonts.font40.getStringWidth("${savedValue.minimum}")
        val nameLength = Fonts.font40.getStringWidth(value.name)
        val sliderWidth = width - 50F - nameLength - maxLength - minLength - valueDisplay
        val startPoint = x + width - 20F - sliderWidth - maxLength - valueDisplay
        if (dragged)
            savedValue.set(round(savedValue.minimum + (savedValue.maximum - savedValue.minimum) / sliderWidth * (mouseX - startPoint)).coerceIn(savedValue.minimum, savedValue.maximum))
        val currLength = Fonts.font40.getStringWidth("${round(savedValue.get())}")
        Fonts.font40.drawString(value.name, x + 10F, y + 10F - Fonts.font40.fontHeight / 2F + 2F, Color(26, 26, 26).getRGB())
        Fonts.font40.drawString("${savedValue.maximum}",
                                x + width - 10F - maxLength - valueDisplay, 
                                y + 10F - Fonts.font40.fontHeight / 2F + 2F, Color(26, 26, 26).getRGB())
        Fonts.font40.drawString("${savedValue.minimum}",
                                x + width - 30F - sliderWidth - maxLength - minLength - valueDisplay, 
                                y + 10F - Fonts.font40.fontHeight / 2F + 2F, Color(26, 26, 26).getRGB())
        slider.setValue(savedValue.get().coerceIn(savedValue.minimum, savedValue.maximum), savedValue.minimum, savedValue.maximum)
        slider.onDraw(x + width - 20F - sliderWidth - maxLength - valueDisplay, y + 10F, sliderWidth, accentColor)
        RenderUtils.drawRoundedRect2(x + width - 5F - valueDisplay, y + 2F, x + width - 10F, y + 18F, 4F, ColorManager.button.rgb)
        RenderUtils.customRounded(x + width - 18F, y + 2F, x + width - 10F, y + 18F, 0F, 4F, 4F, 0F, Color(241, 243, 247).rgb)
        RenderUtils.customRounded(x + width - 5F - valueDisplay, y + 2F, x + width + 3F - valueDisplay, y + 18, 4F, 0F, 0F, 4F, Color(241, 243, 247).rgb)
        Fonts.font40.drawString("${round(savedValue.get())}", x + width + 6F - valueDisplay, y + 10F - Fonts.font40.fontHeight / 2F + 2F, Color(26, 26, 26).getRGB())
        Fonts.font40.drawString("-", x + width - 3F - valueDisplay, y + 10F - Fonts.font40.fontHeight / 2F + 2F, Color(26, 26, 26).getRGB())
        Fonts.font40.drawString("+", x + width - 17F, y + 10F - Fonts.font40.fontHeight / 2F + 2F, Color(26, 26, 26).getRGB())

        return valueHeight
    }

    override fun onClick(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        val valueDisplay = 30F + Fonts.font40.getStringWidth("${savedValue.maximum.toInt().toFloat() + 0.01F}")
        val maxLength = Fonts.font40.getStringWidth("${savedValue.maximum}")
        val minLength = Fonts.font40.getStringWidth("${savedValue.minimum}")
        val nameLength = Fonts.font40.getStringWidth(value.name)
        val sliderWidth = width - 50F - nameLength - maxLength - minLength - valueDisplay
        val startPoint = x + width - 30F - sliderWidth - valueDisplay - maxLength
        val endPoint = x + width - 10F - valueDisplay - maxLength

        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, startPoint, y + 5F, endPoint, y + 15F))
            dragged = true
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 5F - valueDisplay, y + 2F, x + width + 3F - valueDisplay, y + 18F))
            savedValue.set(round(savedValue.get() - 0.01F).coerceIn(savedValue.minimum, savedValue.maximum))
        if (MouseUtils.mouseWithinBounds(mouseX, mouseY, x + width - 18F, y + 2F, x + width - 10F, y + 18F))
            savedValue.set(round(savedValue.get() + 0.01F).coerceIn(savedValue.minimum, savedValue.maximum))
    }

    override fun onRelease(mouseX: Int, mouseY: Int, x: Float, y: Float, width: Float) {
        if (dragged) dragged = false
    }

    private fun round(f: Float): Float = BigDecimal(f.toString()).setScale(2, 4).toFloat()
}