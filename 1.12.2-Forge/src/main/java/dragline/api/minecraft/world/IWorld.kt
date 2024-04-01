/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.world

import dragline.api.minecraft.block.state.IIBlockState
import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.scoreboard.IScoreboard
import dragline.api.minecraft.util.IAxisAlignedBB
import dragline.api.minecraft.util.IMovingObjectPosition
import dragline.api.minecraft.util.WBlockPos
import dragline.api.minecraft.util.WVec3
import dragline.api.minecraft.world.border.IWorldBorder

interface IWorld {
    val isRemote: Boolean
    val scoreboard: IScoreboard
    val worldBorder: IWorldBorder

    fun getEntityByID(id: Int): IEntity?

    fun rayTraceBlocks(start: WVec3, end: WVec3): IMovingObjectPosition?
    fun rayTraceBlocks(start: WVec3, end: WVec3, stopOnLiquid: Boolean): IMovingObjectPosition?
    fun rayTraceBlocks(start: WVec3, end: WVec3, stopOnLiquid: Boolean, ignoreBlockWithoutBoundingBox: Boolean, returnLastUncollidableBlock: Boolean): IMovingObjectPosition?

    fun getEntitiesInAABBexcluding(entityIn: IEntity?, boundingBox: IAxisAlignedBB, predicate: (IEntity?) -> Boolean): Collection<IEntity>
    fun getBlockState(blockPos: WBlockPos): IIBlockState
    fun getEntitiesWithinAABBExcludingEntity(entity: IEntity?, bb: IAxisAlignedBB): Collection<IEntity>
    fun getCollidingBoundingBoxes(entity: IEntity, bb: IAxisAlignedBB): Collection<IAxisAlignedBB>
    fun checkBlockCollision(aabb: IAxisAlignedBB): Boolean
    fun getCollisionBoxes(bb: IAxisAlignedBB): Collection<IAxisAlignedBB>
    fun getChunkFromChunkCoords(x: Int, z: Int): IChunk
}