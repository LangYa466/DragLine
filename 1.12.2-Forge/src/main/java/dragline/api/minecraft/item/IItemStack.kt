/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.item

import dragline.api.minecraft.block.state.IIBlockState
import dragline.api.minecraft.enchantments.IEnchantment
import dragline.api.minecraft.entity.ai.attributes.IAttributeModifier
import dragline.api.minecraft.nbt.INBTBase
import dragline.api.minecraft.nbt.INBTTagCompound
import dragline.api.minecraft.nbt.INBTTagList
import net.minecraft.block.Block

interface IItemStack {
    val displayName: String
    val unlocalizedName: String
    val maxItemUseDuration: Int
    val enchantmentTagList: INBTTagList?
    var tagCompound: INBTTagCompound?
    val stackSize: Int
    var itemDamage: Int
    val item: IItem?
    val itemDelay: Long

    fun getStrVsBlock(block: IIBlockState): Float
    fun getStrVsBlock(block: Block): Float
    fun setTagInfo(key: String, nbt: INBTBase)
    fun setStackDisplayName(displayName: String): IItemStack
    fun addEnchantment(enchantment: IEnchantment, level: Int)
    fun getAttributeModifier(key: String): Collection<IAttributeModifier>
    fun isSplash(): Boolean
}