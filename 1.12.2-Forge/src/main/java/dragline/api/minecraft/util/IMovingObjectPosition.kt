/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.util

import dragline.api.minecraft.client.entity.IEntity

interface IMovingObjectPosition {
    val entityHit: IEntity?
    val blockPos: WBlockPos?
    val sideHit: IEnumFacing?
    val hitVec: WVec3
    val typeOfHit: WMovingObjectType

    enum class WMovingObjectType {
        MISS, ENTITY, BLOCK
    }
}