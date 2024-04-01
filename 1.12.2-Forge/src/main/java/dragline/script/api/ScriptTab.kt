/*
 * DragLine Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/DragLine/
 */

package dragline.script.api

import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.ScriptUtils
import dragline.api.minecraft.item.IItemStack
import dragline.api.util.WrappedCreativeTabs
import dragline.utils.item.ItemUtils

@Suppress("UNCHECKED_CAST", "unused")
class ScriptTab(private val tabObject: JSObject) : WrappedCreativeTabs(tabObject.getMember("name") as String) {
    val items = ScriptUtils.convert(tabObject.getMember("items"), Array<IItemStack>::class.java) as Array<IItemStack>

    override fun getTabIconItem() = ItemUtils.createItem(tabObject.getMember("icon") as String)?.item!!

    override fun getTranslatedTabLabel() = tabObject.getMember("name") as String

    override fun displayAllReleventItems(items: MutableList<IItemStack>) {
        items.forEach { items.add(it) }
    }
}