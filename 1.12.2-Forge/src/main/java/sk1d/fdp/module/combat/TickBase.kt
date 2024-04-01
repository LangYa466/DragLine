/*
 * FDP CLIENT 
 */
package sk1d.fdp.module.combat

import dragline.event.AttackEvent
import dragline.event.UpdateEvent
import dragline.event.EventTarget
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.IntegerValue
import dragline.value.FloatValue
import net.minecraft.entity.EntityLivingBase

@ModuleInfo(name = "TickBase", description = "FDP", category = ModuleCategory.COMBAT)
class TickBase : Module() {

    private var ticks = 0

    val ticksAmount = IntegerValue("BoostTicks", 10, 3, 20)
    val BoostAmount = FloatValue("BoostTimer", 10f, 1f, 50f)
    val ChargeAmount = FloatValue("ChargeTimer", 0.11f, 0.05f, 1f)

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase && ticks == 0) {
            ticks = ticksAmount.get()
        }
    }

    override fun onEnable() {
        mc.timer.timerSpeed = 1f
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }


    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (ticks == ticksAmount.get()) {
            mc.timer.timerSpeed = ChargeAmount.get()
            ticks --
        } else if (ticks > 1) {
            mc.timer.timerSpeed = BoostAmount.get()
            ticks --
        } else if (ticks == 1) {
            mc.timer.timerSpeed = 1f
            ticks --
        }
    }


}