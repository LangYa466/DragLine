/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This part is taken from UnlegitMC/FDPClient. Please credit them when using this in your repository.
 */
package dragline.utils

import dragline.event.ClickWindowEvent
import dragline.event.EventTarget
import dragline.event.Listenable
import dragline.event.PacketEvent
import net.ccbluex.liquidbounce.injection.backend.CPacketPlayerBlockPlacementImpl
import dragline.utils.timer.MSTimer
import net.minecraft.network.play.client.CPacketClientStatus
import net.minecraft.network.play.client.CPacketCloseWindow

object InventoryHelper : MinecraftInstance(), Listenable {
    val CLICK_TIMER = MSTimer()
    //val INV_TIMER = MSTimer()

    @EventTarget
    fun onClickWindow(event: ClickWindowEvent) {
        CLICK_TIMER.reset()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet/*
        if (packet is C0EPacketClickWindow || packet is C08PacketPlayerBlockPlacement) {
            INV_TIMER.reset()
        }*/
        if (packet is CPacketPlayerBlockPlacementImpl<*>) {
            CLICK_TIMER.reset()
        }
    }

    fun openPacket() {
        mc2.connection!!.sendPacket(CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN))
    }

    fun closePacket() {
        mc2.connection!!.sendPacket(CPacketCloseWindow())
    }

    override fun handleEvents() = true
}
