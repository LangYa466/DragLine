package me.nelly.ui

import dragline.DragLine
import dragline.ui.client.hud.element.Border
import dragline.ui.client.hud.element.Element
import dragline.ui.client.hud.element.ElementInfo
import dragline.ui.font.Fonts
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.IntegerValue
import me.nelly.utils.RoundedUtil
import java.awt.Color

@ElementInfo(name = "KeyBinds")
class KeyBinds : Element() {
    val onlyState = BoolValue("OnlyModuleState", false)
    val linevalue = BoolValue("Line", false)
    val cr = IntegerValue("red", 255, 0, 255)
    val cg = IntegerValue("green", 255, 0, 255)
    val cb = IntegerValue("blue", 255, 0, 255)
    val cr2 = IntegerValue("red2", 255, 0, 255)
    val cg2 = IntegerValue("green2", 255, 0, 255)
    val cb2 = IntegerValue("blue2", 255, 0, 255)
    val bga = IntegerValue("bgalpha", 150, 0, 255)
    val shaodwValue = BoolValue("shadow", false)
    val radiusvalue = IntegerValue("radius", 1, 0, 20)

    override fun drawElement(): Border {
        var y2 = 0

        //draw Background
        RoundedUtil.drawRound(
            0f,
            0f,
            90f,
            (19 + getmoduley()).toFloat(),
            radiusvalue.get().toFloat(),
            Color(0, 0, 0, bga.get())
        )

        //shadow
        if (shaodwValue.get()) {
            RenderUtils.drawShadow(0f, 0f, 90f, 19f + getmoduley(), 255f)
        }

        //line
        if (linevalue.get()) {
            RenderUtils.drawGradientSideways(
                2.0,
                14.0,
                88.0,
                15.0,
                Color(cr.get(), cg.get(), cb.get()).rgb,
                Color(cr2.get(), cg2.get(), cb2.get()).rgb
            )
        }
        //draw Title
        Fonts.font35.drawString("KeyBinds", 28f, 5.5f, -1, true)

        //draw Module Bind
        for (module in DragLine.moduleManager.modules) {
            if (module.keyBind == 0) continue
            if (onlyState.get()) {
                if (!module.state) continue
            }
            Fonts.font35.drawString(module.name, 3f, y2 + 21f, -1, true)
            Fonts.font35.drawString(
                "[Toggle]",
                (89 - Fonts.font35.getStringWidth("[Toggle]")).toFloat(),
                y2 + 21f,
                if (module.state) Color(255, 255, 255).rgb else Color(100, 100, 100).rgb,
                true
            )
            y2 += 12
        }
        return Border(0f, 0f, 84f, (17 + getmoduley()).toFloat())
    }

    private fun getmoduley(): Int {
        var y = 0
        for (module in DragLine.moduleManager.modules) {
            if (module.keyBind == 0) continue
            if (onlyState.get()) {
                if (!module.state) continue
            }
            y += 12
        }
        return y
    }
}
