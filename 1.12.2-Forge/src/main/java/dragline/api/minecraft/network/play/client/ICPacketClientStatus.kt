/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.network.play.client

import dragline.api.minecraft.network.IPacket

interface ICPacketClientStatus : IPacket {

    enum class WEnumState {
        PERFORM_RESPAWN, REQUEST_STATS, OPEN_INVENTORY_ACHIEVEMENT
    }
}