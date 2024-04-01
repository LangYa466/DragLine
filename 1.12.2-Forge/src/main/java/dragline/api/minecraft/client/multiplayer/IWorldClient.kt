/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client.multiplayer

import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.client.entity.player.IEntityPlayer
import dragline.api.minecraft.tileentity.ITileEntity
import dragline.api.minecraft.util.WBlockPos
import dragline.api.minecraft.world.IWorld
import net.minecraft.block.state.IBlockState

interface IWorldClient : IWorld {
    val playerEntities: Collection<IEntityPlayer>
    val loadedEntityList: Collection<IEntity>
    val loadedTileEntityList: Collection<ITileEntity>

    fun sendQuittingDisconnectingPacket()
    fun sendBlockBreakProgress(entityId: Int, blockPos: WBlockPos, damage: Int)
    fun addEntityToWorld(entityId: Int, fakePlayer: IEntity)
    fun removeEntityFromWorld(entityId: Int)
    fun setBlockState(blockPos: WBlockPos?, blockstate:IBlockState?, size: Int): Boolean
}