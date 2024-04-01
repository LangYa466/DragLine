/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.network.play.client

import dragline.api.minecraft.network.IPacket
import dragline.api.network.IPacketBuffer

interface ICPacketCustomPayload : IPacket {
    var data: IPacketBuffer
    val channelName: String
}