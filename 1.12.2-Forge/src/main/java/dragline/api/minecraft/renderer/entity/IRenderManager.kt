/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.renderer.entity

import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.client.entity.IEntityLivingBase
import dragline.api.minecraft.tileentity.ITileEntity

interface IRenderManager {
    var isRenderShadow: Boolean
    val viewerPosX: Double
    val viewerPosY: Double
    val viewerPosZ: Double
    val playerViewX: Float
    var playerViewY: Float
    val renderPosX: Double
    val renderPosY: Double
    val renderPosZ: Double

    fun renderEntityStatic(entity: IEntity, renderPartialTicks: Float, hideDebugBox: Boolean): Boolean
    fun renderEntityAt(entity: ITileEntity, x: Double, y: Double, z: Double, partialTicks: Float)
    fun renderEntityWithPosYaw(entityLivingBase: IEntityLivingBase, d: Double, d1: Double, d2: Double, fl: Float, fl1: Float): Boolean
}
