package me.rainyfall.module.combat

import dragline.features.module.modules.combat.KillAura
import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue

@ModuleInfo(name = "KillAuraHelper", description = "KillAuraHelper", category = ModuleCategory.COMBAT)
class KillAuraHelper : Module() {
    private var fovvalue = BoolValue("EnableAirFov", true)
    private var airfov = FloatValue("AirFov", 120F, 100F, 180F).displayable { fovvalue.get() }
    private var fovvalue2 = BoolValue("EnableGroundFov", true)
    private var groundfov = FloatValue("GroundFov", 180F, 100F, 180F).displayable { fovvalue2.get() }
    private var enablegroundrange = BoolValue("EnableGroundRange", true)
    private var enablegroundblockrange = BoolValue("EnableGroundBlockRange", true)
    private var enablegroundhurttime = BoolValue("EnableHurtTime", true)
    private var enableairrange = BoolValue("EnableAirRange", true)
    private var enableairblockrange = BoolValue("EnableAirBlockRange", true)
    private var enableairhurttime = BoolValue("EnableAirHurtTime", true)
    private var groundrange = FloatValue("GroundRange", 3.10F, 1.00F, 4.00F).displayable { enablegroundrange.get() }
    private var groundblockrange =
        FloatValue("GroundBlockRange", 3.20F, 1.00F, 4.00F).displayable { enablegroundblockrange.get() }
    private var groundhurttime = IntegerValue("GroundHurttime", 10, 1, 10).displayable { enablegroundhurttime.get() }
    private var airrange = FloatValue("AirRange", 2.96F, 1.00F, 4.00F).displayable { enableairrange.get() }
    private var airblockrange =
        FloatValue("AirBlockRange", 3.20F, 1.00F, 4.00F).displayable { enableairblockrange.get() }
    private var airhurttime = IntegerValue("AirHurttime", 10, 1, 10).displayable { enableairhurttime.get() }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val aura = DragLine.moduleManager.getModule(KillAura::class.java) as KillAura
        if (!aura.state) return
        if (mc.thePlayer!!.onGround) {
            if (fovvalue2.get()) aura.fovValue.set(groundfov.get())
            if (enablegroundrange.get()) aura.rangeValue.set(groundrange.get())
            if (enablegroundblockrange.get()) aura.blockRangeValue.set(groundblockrange.get())
            if (enablegroundhurttime.get()) aura.hurtTimeValue.set(groundhurttime.get())
        } else {
            if (fovvalue.get()) aura.fovValue.set(airfov.get())
            if (enableairrange.get()) aura.rangeValue.set(airrange.get())
            if (enableairblockrange.get()) aura.blockRangeValue.set(airblockrange.get())
            if (enableairhurttime.get()) aura.hurtTimeValue.set(airhurttime.get())
        }
    }
}
