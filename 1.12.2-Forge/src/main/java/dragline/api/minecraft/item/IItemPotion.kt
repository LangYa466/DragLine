/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.item

import dragline.api.minecraft.potion.IPotionEffect

interface IItemPotion : IItem {
    fun getEffects(stack: IItemStack): Collection<IPotionEffect>
}