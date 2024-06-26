/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.network.play.client.ICPacketCustomPayload
import dragline.api.network.IPacketBuffer
import net.minecraft.network.play.client.CPacketCustomPayload

class CPacketCustomPayloadImpl<T : CPacketCustomPayload>(wrapped: T) : PacketImpl<T>(wrapped), ICPacketCustomPayload {
    override var data: IPacketBuffer
        get() = wrapped.data.wrap()
        set(value) {
            wrapped.data = value.unwrap()
        }
    override val channelName: String
        get() = wrapped.channelName

}

inline fun ICPacketCustomPayload.unwrap(): CPacketCustomPayload = (this as CPacketCustomPayloadImpl<*>).wrapped
inline fun CPacketCustomPayload.wrap(): ICPacketCustomPayload = CPacketCustomPayloadImpl(this)