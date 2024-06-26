package dragline.features.module.modules.world

import dragline.api.minecraft.network.IPacket
import dragline.api.minecraft.network.play.client.ICPacketPlayerDigging
import dragline.api.minecraft.util.IEnumFacing
import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import net.minecraft.init.Blocks


@ModuleInfo(name = "SpeedMine", description = "faq", category = ModuleCategory.WORLD)
class SpeedMine : Module() {
    private var bzs = false
    private var bzx = 0.0f
    var blockPos: WBlockPos? = null
    var facing: IEnumFacing? = null

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (classProvider.isCPacketPlayerDigging(packet)) {
            val packets: IPacket = packet.asCPacketPlayerDigging()
            if (packet.asCPacketPlayerDigging().action == ICPacketPlayerDigging.WAction.START_DESTROY_BLOCK) {
                bzs = true
                blockPos = packets.asCPacketPlayerDigging().position
                facing = packet.asCPacketPlayerDigging().facing
                bzx = 0.0f
            } else if (packet.asCPacketPlayerDigging().action == ICPacketPlayerDigging.WAction.ABORT_DESTROY_BLOCK || packet.asCPacketPlayerDigging().action == ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK) {
                bzs = false
                blockPos = null
                facing = null
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (mc.playerController.extendedReach()) {
            mc.playerController.blockHitDelay = 0
        } else if (bzs) {
            val block = mc.theWorld!!.getBlockState(blockPos!!).block
            bzx += (block.getPlayerRelativeBlockHardness(mc.thePlayer!!, mc.theWorld!!, blockPos!!) * 1.4).toFloat()
            if (bzx >= 1.0f) {
                mc.theWorld!!.setBlockState(blockPos, Blocks.AIR.defaultState, 11)
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK, blockPos!!, facing!!))
                bzx = 0.0f
                bzs = false
            }
        }
    }

}