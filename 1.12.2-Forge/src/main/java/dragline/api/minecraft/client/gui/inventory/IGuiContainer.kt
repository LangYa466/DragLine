/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client.gui.inventory

import dragline.api.minecraft.client.gui.IGuiScreen
import dragline.api.minecraft.inventory.IContainer
import dragline.api.minecraft.inventory.ISlot

interface IGuiContainer : IGuiScreen {
    fun handleMouseClick(slot: ISlot, slotNumber: Int, clickedButton: Int, clickType: Int)

    val inventorySlots: IContainer?
}