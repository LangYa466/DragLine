/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.network

import dragline.api.minecraft.network.play.client.*
import dragline.api.minecraft.network.play.server.*
import dragline.api.minecraft.network.handshake.client.ICPacketHandshake

interface IPacket {
    fun asSPacketChat(): ISPacketChat
    fun asSPacketAnimation(): ISPacketAnimation
    fun asSPacketEntity(): ISPacketEntity
    fun asCPacketPlayer(): ICPacketPlayer
    fun asCPacketUseEntity(): ICPacketUseEntity
    fun asSPacketEntityVelocity(): ISPacketEntityVelocity
    fun asCPacketChatMessage(): ICPacketChatMessage
    fun asSPacketCloseWindow(): ISPacketCloseWindow
    fun asSPacketTabComplete(): ISPacketTabComplete
    fun asSPacketPosLook(): ISPacketPosLook
    fun asSPacketResourcePackSend(): ISPacketResourcePackSend
    fun asCPacketHeldItemChange(): ICPacketHeldItemChange
    fun asSPacketWindowItems(): ISPacketWindowItems
    fun asCPacketCustomPayload(): ICPacketCustomPayload
    fun asCPacketHandshake(): ICPacketHandshake
    fun asCPacketPlayerDigging(): ICPacketPlayerDigging
}