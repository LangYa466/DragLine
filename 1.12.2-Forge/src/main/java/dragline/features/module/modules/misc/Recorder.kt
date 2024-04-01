package dragline.features.module.modules.misc

import me.nelly.module.misc.AutoL
import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.Listenable
import dragline.event.PacketEvent
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.play.server.SPacketTitle

object Recorder : Listenable {
    val autol = DragLine.moduleManager.getModule(AutoL::class.java) as AutoL
    var killCounts = autol.killCounts
    var totalPlayed = 0
    var win = 0
    var ban = autol.ban
    var startTime = System.currentTimeMillis()
    @EventTarget
    private fun onPacket(event: PacketEvent) {
        if (event.packet is C00Handshake) startTime = System.currentTimeMillis()
        val packet = event.packet
        if (packet is SPacketTitle) {
            val title = (packet.message ?: return).formattedText
            if (title.startsWith("§6§l") && title.endsWith("§r") || title.startsWith("§c§lYOU") && title.endsWith(
                    "§r"
                ) || title.startsWith("§c§lGame") && title.endsWith("§r") || title.startsWith("§c§lWITH") && title.endsWith(
                    "§r"
                ) || title.startsWith("§c§lYARR") && title.endsWith("§r")
            ) totalPlayed++
            if (title.startsWith("§6§l") && title.endsWith("§r")) win++
        }
    }

    override fun handleEvents(): Boolean {
        return true
    }
}