/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.misc

import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.timer.MSTimer
import dragline.utils.timer.TimeUtils
import dragline.value.BoolValue
import dragline.value.IntegerValue
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "AtAllProvider", description = "Automatically mentions everyone on the server when using '@a' in your message.", category = ModuleCategory.MISC)
class AtAllProvider : Module() {
    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 500, 0, 20000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelayValue.get()
            if (i < newValue) set(i)
        }
    }
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 1000, 0, 20000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelayValue.get()
            if (i > newValue) set(i)
        }
    }

    private val retryValue = BoolValue("Retry", false)
    private val sendQueue = LinkedBlockingQueue<String>()
    private val retryQueue: MutableList<String> = ArrayList()
    private val msTimer = MSTimer()
    private var delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())

    override fun onDisable() {
        synchronized(sendQueue) {
            sendQueue.clear()
        }
        synchronized(retryQueue) {
            retryQueue.clear()
        }

        super.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (!msTimer.hasTimePassed(delay))
            return

        try {
            synchronized(sendQueue) {
                if (sendQueue.isEmpty()) {
                    if (!retryValue.get() || retryQueue.isEmpty())
                        return
                    else
                        sendQueue.addAll(retryQueue)
                }

                mc.thePlayer!!.sendChatMessage(sendQueue.take())
                msTimer.reset()

                delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (classProvider.isCPacketChatMessage(event.packet)) {
            val packetChatMessage = event.packet.asCPacketChatMessage()
            val message = packetChatMessage.message

            if (message.contains("@a")) {
                synchronized(sendQueue) {
                    for (playerInfo in mc.netHandler.playerInfoMap) {
                        val playerName = playerInfo.gameProfile.name

                        if (playerName == mc.thePlayer!!.name)
                            continue

                        sendQueue.add(message.replace("@a", playerName))
                    }
                    if (retryValue.get()) {
                        synchronized(retryQueue) {
                            retryQueue.clear()
                            retryQueue.addAll(listOf(*sendQueue.toTypedArray()))
                        }
                    }
                }
                event.cancelEvent()
            }
        }
    }
}