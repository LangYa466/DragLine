/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.entity.player

import dragline.api.minecraft.item.IItemStack
import dragline.api.util.IWrappedArray

interface IInventoryPlayer {
    val mainInventory: IWrappedArray<IItemStack?>
    val armorInventory: IWrappedArray<IItemStack?>
    var currentItem: Int
    val offHandInventory: IWrappedArray<IItemStack?>

    fun getStackInSlot(slot: Int): IItemStack?
    fun armorItemInSlot(slot: Int): IItemStack?
    fun getCurrentItemInHand(): IItemStack?
}