/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.item.IItemPotion
import dragline.api.minecraft.item.IItemStack
import dragline.api.minecraft.potion.IPotionEffect
import dragline.api.util.WrappedCollection
import net.minecraft.item.ItemPotion
import net.minecraft.potion.PotionEffect
import net.minecraft.potion.PotionUtils

class ItemPotionImpl(wrapped: ItemPotion) : ItemImpl<ItemPotion>(wrapped), IItemPotion {
    override fun getEffects(stack: IItemStack): Collection<IPotionEffect> {
        return WrappedCollection(
                PotionUtils.getEffectsFromStack(stack.unwrap()),
                IPotionEffect::unwrap,
                PotionEffect::wrap
        )
    }
}


