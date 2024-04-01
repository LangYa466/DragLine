/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.client.entity.player.IEntityPlayer
import dragline.api.minecraft.client.multiplayer.IWorldClient
import dragline.api.minecraft.tileentity.ITileEntity
import dragline.api.minecraft.util.WBlockPos
import dragline.api.util.WrappedCollection
import net.ccbluex.liquidbounce.injection.backend.utils.unwrap
import net.minecraft.block.state.IBlockState
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos

class WorldClientImpl(wrapped: WorldClient) : WorldImpl<WorldClient>(wrapped), IWorldClient {
    override val playerEntities: Collection<IEntityPlayer>
        get() = WrappedCollection(wrapped.playerEntities, IEntityPlayer::unwrap, EntityPlayer::wrap)
    override val loadedEntityList: Collection<IEntity>
        get() = WrappedCollection(wrapped.loadedEntityList, IEntity::unwrap, Entity::wrap)
    override val loadedTileEntityList: Collection<ITileEntity>
        get() = WrappedCollection(wrapped.loadedTileEntityList, ITileEntity::unwrap, TileEntity::wrap)

    override fun sendQuittingDisconnectingPacket() = wrapped.sendQuittingDisconnectingPacket()

    override fun sendBlockBreakProgress(entityId: Int, blockPos: WBlockPos, damage: Int) = wrapped.sendBlockBreakProgress(entityId, blockPos.unwrap(), damage)

    override fun addEntityToWorld(entityId: Int, fakePlayer: IEntity) = wrapped.addEntityToWorld(entityId, fakePlayer.unwrap())

    override fun removeEntityFromWorld(entityId: Int) {
        wrapped.removeEntityFromWorld(entityId)
    }

    override fun setBlockState(blockPos: WBlockPos?, blockstate: IBlockState?, size: Int): Boolean = wrapped.setBlockState(BlockPos(blockPos!!.x,blockPos!!.y,blockPos!!.z),blockstate,size)

}

inline fun IWorldClient.unwrap(): WorldClient = (this as WorldClientImpl).wrapped
inline fun WorldClient.wrap(): IWorldClient = WorldClientImpl(this)