/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.util.IEnumFacing
import dragline.api.minecraft.util.IMovingObjectPosition
import dragline.api.minecraft.util.WBlockPos
import dragline.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.injection.backend.utils.wrap
import net.minecraft.util.math.RayTraceResult

class MovingObjectPositionImpl(val wrapped: RayTraceResult) : IMovingObjectPosition {
    override val entityHit: IEntity?
        get() = wrapped.entityHit?.wrap()
    override val blockPos: WBlockPos?
        get() = wrapped.blockPos?.wrap()
    override val sideHit: IEnumFacing?
        get() = wrapped.sideHit?.wrap()
    override val hitVec: WVec3
        get() = wrapped.hitVec.wrap()
    override val typeOfHit: IMovingObjectPosition.WMovingObjectType
        get() = wrapped.typeOfHit.wrap()


    override fun equals(other: Any?): Boolean {
        return other is MovingObjectPositionImpl && other.wrapped == this.wrapped
    }
}

inline fun IMovingObjectPosition.unwrap(): RayTraceResult = (this as MovingObjectPositionImpl).wrapped
inline fun RayTraceResult.wrap(): IMovingObjectPosition = MovingObjectPositionImpl(this)