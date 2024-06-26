/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.network

import dragline.api.minecraft.item.IItemStack

interface IPacketBuffer {
    fun writeBytes(payload: ByteArray)
    fun writeItemStackToBuffer(itemStack: IItemStack)
    fun writeString(vanilla: String): IPacketBuffer
}