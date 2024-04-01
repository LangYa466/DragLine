package me.nelly.module.movement

import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.movement.Strafe
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue
import net.minecraft.entity.EntityLivingBase

@ModuleInfo(name = "EntitySpeed", description = "", category = ModuleCategory.MOVEMENT)
class EntitySpeed : Module() {
    private val onlyAir = BoolValue("OnlyAir", false)
    private val okstrafe = BoolValue("Strafe", false)
    private val keepSprint = BoolValue("KeepSprint", true)
    private val speedUp = BoolValue("SpeedUp", false)
    private val speed = IntegerValue("Speed", 0, 0, 10)
    private val distance = FloatValue("Range", 0f, 0f, 3f)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val strafe = DragLine.moduleManager.getModule(Strafe::class.java) as Strafe
        for (entity in mc2.world.loadedEntityList) {
            if (entity is EntityLivingBase && entity.entityId != mc2.player.entityId && mc2.player.getDistance(
                    entity
                ) <= distance.get() && (!onlyAir.get() || !mc2.player.onGround)
            ) {
                if (speedUp.get()) {
                    mc2.player.speedInAir *= (1f + (speed.get() * 0.1f))
                }
                if (keepSprint.get()) {
                    mc2.player.isSprinting = true
                }

                if (okstrafe.get()) {
                    strafe.state = true
                }

                return
            }
            strafe.state = false
        }
    }

}