package me.nelly.hackerdetector.checks

import me.nelly.hackerdetector.Category
import me.nelly.hackerdetector.Detection
import dragline.event.EventTarget
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.SPacketChat

class MessageA : Detection("Message A", Category.MISC) {
    private var message: String? = null

    override fun runCheck(player: EntityPlayer): Boolean {
        return message != null && (message!!.contains("我已击杀") ||
                message!!.contains("我正在使用") ||
                message!!.contains("私人") ||
                message!!.contains("参数") ||
                message!!.contains("配置"))
    }

    @EventTarget
    fun onPacket(event: SPacketChat) {
        message = event.chatComponent.unformattedText
    }
}
