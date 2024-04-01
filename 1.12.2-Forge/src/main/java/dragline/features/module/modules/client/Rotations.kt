/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.client

import me.kid.Scaffold2
import me.rainyfall.module.world.Scaffold3
import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.Render3DEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.combat.KillAura
import dragline.features.module.modules.world.*
import dragline.utils.RotationUtils
import dragline.value.BoolValue
import me.nelly.module.world.BlockFly
import sk1d.fdp.module.world.Scaffold4
import sk1d.unknow.module.world.Scaffold5

@ModuleInfo(name = "Rotations", description = "Allows you to see server-sided head and body rotations.", category = ModuleCategory.CLIENT)
class Rotations : Module() {

    private val bodyValue = BoolValue("Body", true)

    private var playerYaw: Float? = null

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (RotationUtils.serverRotation != null && !bodyValue.get())
            mc.thePlayer?.rotationYawHead = RotationUtils.serverRotation.yaw
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer

        if (!bodyValue.get() || !shouldRotate() || thePlayer == null)
            return

        val packet = event.packet

        if (classProvider.isCPacketPlayerPosLook(packet) || classProvider.isCPacketPlayerLook(packet)) {
            val packetPlayer = packet.asCPacketPlayer()

            playerYaw = packetPlayer.yaw

            thePlayer.renderYawOffset = packetPlayer.yaw
            thePlayer.rotationYawHead = packetPlayer.yaw
        } else {
            if (playerYaw != null)
                thePlayer.renderYawOffset = this.playerYaw!!


            thePlayer.rotationYawHead = thePlayer.renderYawOffset
        }
    }

    private fun getState(module: Class<*>) = DragLine.moduleManager[module]!!.state

    private fun shouldRotate(): Boolean {
        val killAura = DragLine.moduleManager.getModule(KillAura::class.java) as KillAura
        return getState(Scaffold::class.java) || getState(Tower::class.java) ||
                (getState(KillAura::class.java) && killAura.target != null) ||
                getState(Scaffold2::class.java) || getState(Scaffold3::class.java) || getState(Scaffold4::class.java) || getState(Scaffold5::class.java) || getState(BlockFly::class.java) ||
                getState(Fucker::class.java) || getState(CivBreak::class.java) || getState(Nuker::class.java) ||
                getState(ChestAura::class.java)
    }
}
