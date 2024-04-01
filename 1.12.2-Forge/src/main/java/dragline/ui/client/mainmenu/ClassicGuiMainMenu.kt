/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package dragline.ui.client.mainmenu

import nellyobfuscator.NellyClassObfuscator
import dragline.DragLine
import dragline.api.minecraft.client.gui.IGuiButton
import dragline.api.util.WrappedGuiScreen
import dragline.ui.client.GuiBackground
import dragline.ui.client.altmanager.GuiAltManager
import net.minecraft.client.gui.*
import java.awt.Color
@NellyClassObfuscator
class ClassicGuiMainMenu : WrappedGuiScreen(), GuiYesNoCallback {

    override fun initGui() {
        val defaultHeight = (representedScreen.height / 3.5).toInt()

        representedScreen.buttonList.add(classProvider.createGuiButton(1, representedScreen.width / 2 - 50, defaultHeight, 100, 20, "SinglePlayer"))
        representedScreen.buttonList.add(classProvider.createGuiButton(2, representedScreen.width / 2 - 50, defaultHeight + 24, 100, 20, "Multiplayer"))
        representedScreen.buttonList.add(classProvider.createGuiButton(100, representedScreen.width / 2 - 50, defaultHeight + 24 * 2, 100, 20, "Altmanager"))
        representedScreen.buttonList.add(classProvider.createGuiButton(102, representedScreen.width / 2 - 50, defaultHeight + 24 * 3, 100, 20, "Background"))
        representedScreen.buttonList.add(classProvider.createGuiButton(0, representedScreen.width / 2 - 50, defaultHeight + 24 * 4, 100, 20, "Options"))
        representedScreen.buttonList.add(classProvider.createGuiButton(4, representedScreen.width / 2 - 50, defaultHeight + 24 * 5, 100, 20, "Quit"))

        super.initGui()
    }


    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        representedScreen.drawBackground(0)

        val bHeight = (representedScreen.height / 3.5).toInt()

        Gui.drawRect(representedScreen.width / 2 - 60, bHeight - 30, representedScreen.width / 2 + 60, bHeight + 174, Integer.MIN_VALUE)

        mc.fontRendererObj.drawCenteredString(DragLine.CLIENT_NAME, (representedScreen.width / 2).toFloat(), (bHeight - 20).toFloat(), Color.WHITE.rgb, false)
        mc.fontRendererObj.drawString(DragLine.CLIENT_VERSION.toString(), 3F, (representedScreen.height - mc.fontRendererObj.fontHeight - 2).toFloat(), 0xffffff, false)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: IGuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(classProvider.createGuiOptions(this.representedScreen, mc.gameSettings))
            1 -> mc.displayGuiScreen(classProvider.createGuiSelectWorld(this.representedScreen))
            2 -> mc.displayGuiScreen(classProvider.createGuiMultiplayer(this.representedScreen))
            4 -> mc.shutdown()
            100 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiAltManager(this.representedScreen)))
            102 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiBackground(this.representedScreen)))
        }
    }

    override fun confirmClicked(p0: Boolean, p1: Int) {
    }
}