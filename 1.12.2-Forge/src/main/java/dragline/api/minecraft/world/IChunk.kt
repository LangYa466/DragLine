/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.world

import dragline.api.minecraft.block.state.IIBlockState
import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.client.entity.IEntityPlayerSP
import dragline.api.minecraft.util.IAxisAlignedBB
import dragline.api.minecraft.util.WBlockPos

interface IChunk {
    val x: Int
    val z: Int

    fun getEntitiesWithinAABBForEntity(thePlayer: IEntityPlayerSP, arrowBox: IAxisAlignedBB, collidedEntities: MutableList<IEntity>, nothing: Nothing?)
    fun getHeightValue(x: Int, z: Int): Int
    fun getBlockState(blockPos: WBlockPos): IIBlockState
}