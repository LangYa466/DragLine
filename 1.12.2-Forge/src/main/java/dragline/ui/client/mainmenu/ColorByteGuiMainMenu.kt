/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte
 */
package dragline.ui.client.mainmenu


import nellyobfuscator.NellyClassObfuscator
import dragline.api.util.WrappedGuiScreen
import dragline.DragLine
import dragline.api.minecraft.client.gui.IGuiButton
import dragline.ui.client.GuiBackground
import dragline.ui.client.GuiContributors
import dragline.ui.client.GuiModsMenu
import dragline.ui.client.GuiServerStatus
import dragline.ui.client.altmanager.GuiAltManager
import dragline.ui.font.Fonts
import dragline.utils.render.RenderUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
@NellyClassObfuscator
class ColorByteGuiMainMenu : WrappedGuiScreen() {
    private var currentX = 0f
    private var currentY = 0f
    override fun initGui() {
        val defaultHeight = representedScreen.height / 4.5 + 18

        representedScreen.buttonList.add(
            classProvider.createGuiButton(
                1, representedScreen.width - 120,
                (defaultHeight + 24).toInt(), 100, 20, "SinglePlayer"
            )
        )
        representedScreen.buttonList.add(
            classProvider.createGuiButton(
                2, representedScreen.width - 120,
                (defaultHeight + 48).toInt(), 100, 20, "MultiPlayer"
            )
        )
        representedScreen.buttonList.add(
            classProvider.createGuiButton(
                102, representedScreen.width - 120,
                (defaultHeight + 72).toInt(), 100, 20, "Background"
            )
        )
        representedScreen.buttonList.add(
            classProvider.createGuiButton(
                100, representedScreen.width - 120,
                (defaultHeight + 96).toInt(), 100, 20, "AltManager"
            )
        )
        representedScreen.buttonList.add(
            classProvider.createGuiButton(
                0, representedScreen.width - 120,
                (defaultHeight + 120).toInt(), 100, 20, "Options"
            )
        )
        representedScreen.buttonList.add(
            classProvider.createGuiButton(
                4, representedScreen.width - 120,
                (defaultHeight + 144).toInt(), 100, 20, "Exit"
            )
        )
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        val height = representedScreen.height

        val h = representedScreen.height
        val w = representedScreen.width
        val res = ScaledResolution(mc2)
        val xDiff: Float = ((mouseX - h / 2).toFloat() - this.currentX) / res.scaleFactor.toFloat()
        val yDiff: Float = ((mouseY - w / 2).toFloat() - this.currentY) / res.scaleFactor.toFloat()
        this.currentX += xDiff * 0.3f
        this.currentY += yDiff * 0.3f
        GlStateManager.translate(this.currentX / 30.0f, this.currentY / 15.0f, 0.0f)
        representedScreen.drawBackground(0)
        GlStateManager.translate(-this.currentX / 30.0f, -this.currentY / 15.0f, 0.0f)

        RenderUtils.drawRect(
            representedScreen.width - 142,
            0,
            representedScreen.width,
            representedScreen.height, Color(0, 0, 0, 80).rgb
        )
        Fonts.fontBold95.drawCenteredString(
            DragLine.CLIENT_NAME,
            ((95 / 2) - (Fonts.fontBold95.getStringWidth(DragLine.CLIENT_NAME) / 2) + representedScreen.width - 70.5).toFloat(),
            representedScreen.height / 6F, Color.WHITE.rgb, true
        )

        Fonts.minecraftFont.drawString("Minecraft 1.12.2", 4f, height - 12f, Color(255, 255, 255, 200).rgb, true)

        representedScreen.superDrawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: IGuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(classProvider.createGuiOptions(this.representedScreen, mc.gameSettings))
            1 -> mc.displayGuiScreen(classProvider.createGuiSelectWorld(this.representedScreen))
            2 -> mc.displayGuiScreen(classProvider.createGuiMultiplayer(this.representedScreen))
            4 -> mc.shutdown()
            100 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiAltManager(this.representedScreen)))
            101 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiServerStatus(this.representedScreen)))
            102 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiBackground(this.representedScreen)))
            103 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiModsMenu(this.representedScreen)))
            108 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiContributors(this.representedScreen)))
        }
    }
}