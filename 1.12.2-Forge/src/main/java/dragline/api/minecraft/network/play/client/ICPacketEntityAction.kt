/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.network.play.client

import dragline.api.minecraft.network.IPacket

interface ICPacketEntityAction : IPacket {

    enum class WAction {
        START_SNEAKING, STOP_SNEAKING, STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, OPEN_INVENTORY
    }

}