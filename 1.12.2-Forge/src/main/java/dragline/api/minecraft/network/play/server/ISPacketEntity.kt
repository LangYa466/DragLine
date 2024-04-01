/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.network.play.server

import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.network.IPacket
import dragline.api.minecraft.world.IWorld

interface ISPacketEntity : IPacket {
    val onGround: Boolean

    fun getEntity(world: IWorld): IEntity?
}