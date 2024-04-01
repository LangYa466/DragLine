/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client.multiplayer

import dragline.api.minecraft.client.entity.IEntityPlayerSP
import dragline.api.minecraft.client.entity.player.IEntityPlayer
import dragline.api.minecraft.item.IItemStack
import dragline.api.minecraft.util.IEnumFacing
import dragline.api.minecraft.util.WBlockPos
import dragline.api.minecraft.util.WVec3
import dragline.api.minecraft.world.IWorld
import dragline.api.minecraft.world.IWorldSettings

interface IPlayerControllerMP {
    val isNotCreative: Boolean
    val blockReachDistance: Float
    val currentGameType: IWorldSettings.WGameType
    val isInCreativeMode: Boolean
    var curBlockDamageMP: Float
    var blockHitDelay: Int


    fun windowClick(windowId: Int, slot: Int, mouseButton: Int, mode: Int, player: IEntityPlayerSP)
    fun updateController()
    fun sendUseItem(playerSP: IEntityPlayer, theWorld: IWorld, itemStack: IItemStack): Boolean
    fun onPlayerRightClick(playerSP: IEntityPlayerSP, theWorld: IWorldClient, itemStack: IItemStack?, position: WBlockPos, sideOpposite: IEnumFacing, hitVec: WVec3): Boolean
    fun onStoppedUsingItem(thePlayer: IEntityPlayerSP)
    fun clickBlock(blockPos: WBlockPos, enumFacing: IEnumFacing): Boolean
    fun onPlayerDestroyBlock(blockPos: WBlockPos, enumFacing: IEnumFacing): Boolean
    fun extendedReach() : Boolean
}