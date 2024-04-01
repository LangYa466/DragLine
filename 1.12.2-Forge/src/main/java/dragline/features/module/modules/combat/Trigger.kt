/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.combat

import dragline.event.EventTarget
import dragline.event.Render3DEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.EntityUtils
import dragline.utils.timer.TimeUtils
import dragline.value.IntegerValue

@ModuleInfo(name = "Trigger", description = "Automatically attacks the entity you are looking at.", category = ModuleCategory.COMBAT)
class Trigger : Module() {

    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)
            delay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)
            delay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }

    private var delay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
    private var lastSwing = 0L

    @EventTarget
    fun onRender(event: Render3DEvent) {
        val objectMouseOver = mc.objectMouseOver

        if (objectMouseOver != null && System.currentTimeMillis() - lastSwing >= delay &&
                EntityUtils.isSelected(objectMouseOver.entityHit, true)) {
            mc.gameSettings.keyBindAttack.onTick(mc.gameSettings.keyBindAttack.keyCode) // Minecraft Click handling

            lastSwing = System.currentTimeMillis()
            delay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
    }
}