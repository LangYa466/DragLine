/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.network.play.server

import dragline.api.minecraft.network.IPacket

interface ISPacketWindowItems : IPacket {
    val windowId: Int
}