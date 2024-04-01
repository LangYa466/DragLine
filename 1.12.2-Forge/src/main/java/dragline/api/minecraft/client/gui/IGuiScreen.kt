/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client.gui

import dragline.api.minecraft.client.gui.inventory.IGuiChest
import dragline.api.minecraft.client.gui.inventory.IGuiContainer

interface IGuiScreen : IGui {
    val width: Int
    val height: Int
    val fontRendererObj: IFontRenderer
    val buttonList: MutableList<IGuiButton>

    fun asGuiContainer(): IGuiContainer
    fun asGuiGameOver(): IGuiGameOver
    fun asGuiChest(): IGuiChest

    // Non-virtual calls. Used for GuiScreen-Wrapping
    fun superMouseReleased(mouseX: Int, mouseY: Int, state: Int)
    fun drawBackground(i: Int)
    fun drawDefaultBackground()
    fun superKeyTyped(typedChar: Char, keyCode: Int)
    fun superHandleMouseInput()
    fun superMouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int)
    fun superDrawScreen(mouseX: Int, mouseY: Int, partialTicks: Float)
}