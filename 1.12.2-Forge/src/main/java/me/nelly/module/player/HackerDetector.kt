package me.nelly.module.player

import me.nelly.hackerdetector.DetectionManager
import dragline.DragLine
import dragline.event.TickEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.ui.client.hud.element.elements.Notification
import dragline.ui.client.hud.element.elements.NotifyType
import dragline.value.BoolValue
import net.minecraft.entity.player.EntityPlayer

@ModuleInfo(name = "HackerDetector", description = "Detects if you are a hacker", category = ModuleCategory.PLAYER)
class HackerDetector : Module() {
    private val detectionManager: DetectionManager = DetectionManager()
    private val flighta = BoolValue("Flight A", true)
    private val flightb = BoolValue("Flight B", true)
    private val reacha = BoolValue("Reach A", true)
    private val killauraa = BoolValue("KillAura A", true)


    fun onTick(event: TickEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        for (entity in mc.theWorld!!.loadedTileEntityList) {
            if (entity is EntityPlayer) {
                val entityPlayer: EntityPlayer = entity
                if (entityPlayer !== mc.thePlayer) {
                    for (d in detectionManager.detections) {
                        if (flighta.get() && flightb.get() && reacha.get() && killauraa.get())
                            if (d.runCheck(entityPlayer) && System.currentTimeMillis() > d.lastViolated + 500) {
                                DragLine.hud.addNotification(
                                    Notification(
                                        entityPlayer.name,
                                        ("has flagged " + d.name),
                                        NotifyType.WARNING
                                    )
                                )
                            }
                    }
                }
            }
        }
    }
}
