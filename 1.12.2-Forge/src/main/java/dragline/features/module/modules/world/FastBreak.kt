/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.world

import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.FloatValue

@ModuleInfo(name = "FastBreak", description = "Allows you to break blocks faster.", category = ModuleCategory.WORLD)
class FastBreak : Module() {

    private val breakDamage = FloatValue("BreakDamage", 0.8F, 0.1F, 1F)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.playerController.blockHitDelay = 0

        if (mc.playerController.curBlockDamageMP > breakDamage.get())
            mc.playerController.curBlockDamageMP = 1F

        if (Fucker.currentDamage > breakDamage.get())
            Fucker.currentDamage = 1F

        if (Nuker.currentDamage > breakDamage.get())
            Nuker.currentDamage = 1F
    }
}
