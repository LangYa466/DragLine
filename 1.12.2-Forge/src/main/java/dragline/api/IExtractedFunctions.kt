/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api

import com.mojang.authlib.GameProfile
import dragline.api.minecraft.client.block.IBlock
import dragline.api.minecraft.enchantments.IEnchantment
import dragline.api.minecraft.entity.IEnumCreatureAttribute
import dragline.api.minecraft.inventory.ISlot
import dragline.api.minecraft.item.IItem
import dragline.api.minecraft.item.IItemStack
import dragline.api.minecraft.potion.IPotion
import dragline.api.minecraft.scoreboard.ITeam
import dragline.api.minecraft.tileentity.ITileEntity
import dragline.api.minecraft.util.IIChatComponent
import dragline.api.minecraft.util.IResourceLocation

interface IExtractedFunctions {
    fun getBlockById(id: Int): IBlock?
    fun getIdFromBlock(block: IBlock): Int
    fun getModifierForCreature(heldItem: IItemStack?, creatureAttribute: IEnumCreatureAttribute): Float
    fun getObjectFromItemRegistry(res: IResourceLocation): IItem?
    fun renderTileEntity(tileEntity: ITileEntity, partialTicks: Float, destroyStage: Int)
    fun getBlockFromName(name: String): IBlock?
    fun getItemByName(name: String): IItem?
    fun getEnchantmentByLocation(location: String): IEnchantment?
    fun getEnchantmentById(enchantID: Int): IEnchantment?
    fun getEnchantments(): Collection<IResourceLocation>
    fun getItemRegistryKeys(): Collection<IResourceLocation>
    fun getBlockRegistryKeys(): Collection<IResourceLocation>
    fun disableStandardItemLighting()
    fun formatI18n(key: String, vararg values: String): String
    fun sessionServiceJoinServer(profile: GameProfile, token: String, sessionHash: String)
    fun getPotionById(potionID: Int): IPotion
    fun enableStandardItemLighting()
    fun scoreboardFormatPlayerName(scorePlayerTeam: ITeam?, playerName: String): String
    fun disableFastRender()
    fun jsonToComponent(toString: String): IIChatComponent
    fun setActiveTextureLightMapTexUnit()
    fun setActiveTextureDefaultTexUnit()
    fun getItemById(id: Int): IItem?
    fun getIdFromItem(sb: IItem): Int
    fun canAddItemToSlot(slotIn: ISlot, stack: IItemStack, stackSizeMatters: Boolean): Boolean

}
