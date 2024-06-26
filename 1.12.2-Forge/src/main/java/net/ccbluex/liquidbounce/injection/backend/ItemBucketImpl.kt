/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.client.block.IBlock
import dragline.api.minecraft.item.IItemBucket
import net.minecraft.item.ItemBucket

class ItemBucketImpl(wrapped: ItemBucket) : ItemImpl<ItemBucket>(wrapped), IItemBucket {
    override val isFull: IBlock
        get() = BlockImpl(wrapped.containedBlock)
}