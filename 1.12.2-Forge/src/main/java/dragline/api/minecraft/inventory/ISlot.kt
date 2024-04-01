/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.inventory

import dragline.api.minecraft.item.IItemStack

interface ISlot {
    val slotNumber: Int
    val stack: IItemStack?
    val hasStack: Boolean

}