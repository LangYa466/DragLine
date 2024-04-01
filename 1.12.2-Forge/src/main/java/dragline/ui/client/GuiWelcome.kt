package dragline.ui.client

import dragline.DragLine
import dragline.ui.client.mainmenu.GuiMainMenuChoose
import dragline.ui.font.Fonts
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiYesNoCallback
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.io.IOException

class GuiWelcome : GuiScreen(), GuiYesNoCallback {
    var curAlpha = 255
    var alpha = 255

    override fun drawScreen(p_drawScreen_1_: Int, p_drawScreen_2_: Int, p_drawScreen_3_: Float) {
        val Height: Int
        Height = ScaledResolution(mc).scaledHeight
        val Width: Int
        Width = ScaledResolution(mc).scaledWidth
        var text: String
        val Scale: Float
        if (curAlpha > alpha) curAlpha -= 20
        if (curAlpha < alpha) curAlpha += 20
        if (alpha - curAlpha < 20 && alpha - curAlpha > -20) curAlpha = alpha
        if (curAlpha == 0) return
        GL11.glPushMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        drawRect(0, 0, Width, Height, Color(0, 0, 0, Math.min(160, curAlpha)).rgb)
        text =
            "Hello "  + mc.session.username+" Welcome back to ${DragLine.CLIENT_NAME}"
        Scale = 2.0f
        GL11.glScaled(Scale.toDouble(), Scale.toDouble(), Scale.toDouble())
        Fonts.font40.drawString(
            text, (Width / 2f - Fonts.font40.getStringWidth(text)) / Scale,
            (Height / 2 - 9) / Scale, Color.WHITE.rgb
        )
        GL11.glScaled(1.0 / Scale, 1.0 / Scale, 1.0 / Scale)
        text = "Click here to continue..."
        Fonts.font40.drawString(
            text, Width / 2.0f - Fonts.font40.getStringWidth(text) / 2.0f,
            (Height / 2 + 11).toFloat(), Color.WHITE.rgb
        )
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1f)
        GL11.glPopMatrix()
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_)
    }
    override fun initGui() {

    }


    override fun mouseClicked(p_mouseClicked_1_: Int, p_mouseClicked_2_: Int, p_mouseClicked_3_: Int) {
        alpha = 0
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_)
    }

}