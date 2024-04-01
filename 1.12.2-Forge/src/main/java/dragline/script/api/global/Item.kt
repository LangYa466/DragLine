/*
 * DragLine Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/DragLine/
 */

package dragline.script.api.global

import dragline.api.minecraft.item.IItemStack
import dragline.utils.item.ItemUtils

/**
 * Object used by the script API to provide an easier way of creating items.
 */
object Item {

    /**
     * Creates an item.
     * @param itemArguments Arguments describing the item.
     * @return An instance of [ItemStack] with the given data.
     */
    @JvmStatic
    fun create(itemArguments: String): IItemStack = ItemUtils.createItem(itemArguments)
}