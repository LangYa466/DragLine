/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.network.play.client

import dragline.api.minecraft.network.IPacket
import dragline.api.minecraft.util.IEnumFacing
import dragline.api.minecraft.util.WBlockPos

interface ICPacketPlayerDigging : IPacket {

    val position: WBlockPos
    val facing: IEnumFacing
    val action: WAction

    enum class WAction {
        START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK, DROP_ALL_ITEMS, DROP_ITEM, RELEASE_USE_ITEM, SWAP_HELD_ITEMS
    }
}