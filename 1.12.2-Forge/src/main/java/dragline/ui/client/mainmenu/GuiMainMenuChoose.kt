// By Nel1y
package dragline.ui.client.mainmenu;

import nellyobfuscator.NellyClassObfuscator
import dragline.api.minecraft.client.gui.IGuiButton
import dragline.ui.client.GuiMainMenu
import dragline.api.util.WrappedGuiScreen
import kotlin.system.exitProcess

@NellyClassObfuscator
class GuiMainMenuChoose : WrappedGuiScreen() {
        override fun initGui() {
            representedScreen.buttonList.add(
                classProvider.createGuiButton(
                    0,
                    representedScreen.width / 2 - 100,
                    representedScreen.height / 4 + 48,
                    "FDPGuiMainMenu"
                )
            )
            representedScreen.buttonList.add(
                classProvider.createGuiButton(
                    1,
                    representedScreen.width / 2 - 100,
                    representedScreen.height / 4 + 48 + 25,
                    "ColorByteGuiMainMenu"
                )
            )
            representedScreen.buttonList.add(
                classProvider.createGuiButton(
                    2,
                    representedScreen.width / 2 - 100,
                    representedScreen.height / 4 + 48 + 50,
                    "LBGuiMainMenu"
                )
            )
            representedScreen.buttonList.add(
                classProvider.createGuiButton(
                    3,
                    representedScreen.width / 2 - 100,
                    representedScreen.height / 4 + 48 + 75,
                    "Exit"
                )
            )
        }
    override fun actionPerformed(button: IGuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(ClassicGuiMainMenu()))
            1 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(ColorByteGuiMainMenu()))
            2 -> mc.displayGuiScreen(classProvider.wrapGuiScreen(GuiMainMenu()))
            3 -> while(true) exitProcess(0)
        }
    }
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        representedScreen.drawBackground(1)
        representedScreen.superDrawScreen(mouseX, mouseY, partialTicks)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }
}