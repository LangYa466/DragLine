/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.block.state.IIBlockState
import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.client.entity.IEntityPlayerSP
import dragline.api.minecraft.util.IAxisAlignedBB
import dragline.api.minecraft.util.WBlockPos
import dragline.api.minecraft.world.IChunk
import dragline.api.util.WrappedMutableList
import net.ccbluex.liquidbounce.injection.backend.utils.unwrap
import net.minecraft.entity.Entity
import net.minecraft.world.chunk.Chunk

class ChunkImpl(val wrapped: Chunk) : IChunk {
    override val x: Int
        get() = wrapped.x
    override val z: Int
        get() = wrapped.z

    override fun getEntitiesWithinAABBForEntity(thePlayer: IEntityPlayerSP, arrowBox: IAxisAlignedBB, collidedEntities: MutableList<IEntity>, nothing: Nothing?) {
        return wrapped.getEntitiesWithinAABBForEntity(thePlayer.unwrap(), arrowBox.unwrap(), WrappedMutableList(collidedEntities, Entity::wrap, IEntity::unwrap), null)
    }

    override fun getHeightValue(x: Int, z: Int): Int = wrapped.getHeightValue(x, z)

    override fun getBlockState(blockPos: WBlockPos): IIBlockState = wrapped.getBlockState(blockPos.unwrap()).wrap()

    override fun equals(other: Any?): Boolean {
        return other is ChunkImpl && other.wrapped == this.wrapped
    }
}

inline fun IChunk.unwrap(): Chunk = (this as ChunkImpl).wrapped
inline fun Chunk.wrap(): IChunk = ChunkImpl(this)