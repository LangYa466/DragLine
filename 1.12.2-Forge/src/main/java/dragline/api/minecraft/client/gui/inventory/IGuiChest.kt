/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client.gui.inventory

interface IGuiChest : IGuiContainer {
    val inventoryRows: Int
    val lowerChestInventory: IIInventory?
}