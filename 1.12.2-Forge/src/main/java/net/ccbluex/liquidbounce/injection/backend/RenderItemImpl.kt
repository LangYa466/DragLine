/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.client.gui.IFontRenderer
import dragline.api.minecraft.client.render.entity.IRenderItem
import dragline.api.minecraft.item.IItemStack
import net.minecraft.client.renderer.RenderItem

class RenderItemImpl(val wrapped: RenderItem) : IRenderItem {
    override var zLevel: Float
        get() = wrapped.zLevel
        set(value) {
            wrapped.zLevel = value
        }

    override fun renderItemAndEffectIntoGUI(stack: IItemStack, x: Int, y: Int) = wrapped.renderItemAndEffectIntoGUI(stack.unwrap(), x, y)

    override fun renderItemIntoGUI(stack: IItemStack, x: Int, y: Int) = wrapped.renderItemIntoGUI(stack.unwrap(), x, y)

    override fun renderItemOverlays(fontRenderer: IFontRenderer, stack: IItemStack, x: Int, y: Int) = wrapped.renderItemOverlays(fontRenderer.unwrap(), stack.unwrap(), x, y)


    override fun equals(other: Any?): Boolean {
        return other is RenderItemImpl && other.wrapped == this.wrapped
    }
}

inline fun IRenderItem.unwrap(): RenderItem = (this as RenderItemImpl).wrapped
inline fun RenderItem.wrap(): IRenderItem = RenderItemImpl(this)