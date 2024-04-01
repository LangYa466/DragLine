package me.nelly.module.misc

import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.TickEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.ClientUtils
import dragline.utils.timer.MSTimer
import dragline.value.FloatValue

@ModuleInfo(name = "MemoryClear", category = ModuleCategory.MISC, description = "Nel1y")
class MemoryClear : Module() {
    private val delay: FloatValue = FloatValue("Delay", 120.0f, 10.0f, 600.0f)
    private val limit: FloatValue = FloatValue("Limit", 80.0f, 20.0f, 95.0f)
    var timer: MSTimer = MSTimer()

    @EventTarget
    fun onTick(event: TickEvent?) {
        val maxMem = Runtime.getRuntime().maxMemory()
        val totalMem = Runtime.getRuntime().totalMemory()
        val freeMem = Runtime.getRuntime().freeMemory()
        val usedMem = totalMem - freeMem
        val pct = (usedMem * 100L / maxMem).toFloat()
        if (timer.hasReached(delay.get() * 1000.0) && limit.get() <= pct.toDouble()) {
            System.gc()
            ClientUtils.displayChatMessage(DragLine.CLIENT_NAME + " | 内存清理成功")
        }
    }
}
