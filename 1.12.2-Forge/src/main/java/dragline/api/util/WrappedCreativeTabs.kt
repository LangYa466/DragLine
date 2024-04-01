/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.util

import dragline.DragLine
import dragline.api.enums.ItemType
import dragline.api.minecraft.creativetabs.ICreativeTabs
import dragline.api.minecraft.item.IItem
import dragline.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.injection.backend.WrapperImpl.classProvider

abstract class WrappedCreativeTabs(val name: String) {
    lateinit var representedType: ICreativeTabs

    init {
        DragLine.wrapper.classProvider.wrapCreativeTab(name, this)
    }

    open fun displayAllReleventItems(items: MutableList<IItemStack>) {}
    open fun getTranslatedTabLabel(): String = "asdf"
    open fun getTabIconItem(): IItem = classProvider.getItemEnum(ItemType.WRITABLE_BOOK)
    open fun hasSearchBar(): Boolean = true
}