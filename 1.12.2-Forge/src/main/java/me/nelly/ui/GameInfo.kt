package me.nelly.ui

import dragline.DragLine
import dragline.ui.client.hud.element.Border
import dragline.ui.client.hud.element.Element
import dragline.ui.client.hud.element.ElementInfo
import dragline.ui.client.hud.element.elements.Text.Companion.HOUR_FORMAT
import dragline.ui.font.Fonts
import dragline.utils.CPSCounter
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.ListValue
import me.nelly.module.misc.AutoL
import me.nelly.utils.RoundedUtil
import java.awt.Color

// By nelly 仿Asaka
@ElementInfo(name = "GameInfo")
class GameInfo : Element() {

    val modeValue = ListValue("Mode", arrayOf("Level", "CPS", "Times", "Kills", "Bans"), "Times")
    private val shadowValue = BoolValue("Shadow", true)
    private val radiusValue = FloatValue("Radius", 2.0f, 0.0f, 10.0f)

    override fun drawElement(): Border {
        val autoL = DragLine.moduleManager.getModule(AutoL::class.java) as AutoL
        val kills = autoL.killCounts
        val bans = autoL.ban
        var modeString = ""
        if (modeValue.get().equals("Times")) {
            modeString = "Times: ${HOUR_FORMAT.format(System.currentTimeMillis())}"
        }

        if (modeValue.get().equals("CPS")) {
            modeString = "CPS: ${CPSCounter.getCPS(CPSCounter.MouseButton.LEFT)}"
        }

        if (modeValue.get().equals("Level")) {
            modeString = "Level: ${mc2.player.experienceLevel}"
        }

        if (modeValue.get().equals("Kills")) {
            modeString = "Kills: $kills"
        }

        if (modeValue.get().equals("Bans")) {
            modeString = "Bans: $bans"
        }

        RoundedUtil.drawRound(
            0f,
            0f,
            Fonts.font35.getStringWidth(modeString).toFloat(),
            Fonts.font35.fontHeight.toFloat(),
            radiusValue.get(),
            Color(Color.BLACK.red, Color.BLACK.green, Color.BLACK.blue, 90)
        )

        if (shadowValue.get()) {
            RenderUtils.drawShadow(
                0f,
                0f,
                Fonts.font35.getStringWidth(modeString).toFloat(),
                Fonts.font35.fontHeight.toFloat() + 6,
                255f
            )
        }

        Fonts.font35.drawString(modeString, 0f, 0f, -1)

        return Border(
            0f,
            0f,
            Fonts.font35.getStringWidth(modeString).toFloat(),
            Fonts.font35.fontHeight.toFloat() + 6
        )
    }
}
