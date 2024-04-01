/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.movement

import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.block.BlockUtils.getBlock
import dragline.value.FloatValue

@ModuleInfo(name = "WaterSpeed", description = "Allows you to swim faster.", category = ModuleCategory.MOVEMENT)
class WaterSpeed : Module() {
    private val speedValue = FloatValue("Speed", 1.2f, 1.1f, 1.5f)

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater && classProvider.isBlockLiquid(getBlock(thePlayer.position))) {
            val speed = speedValue.get()

            thePlayer.motionX *= speed
            thePlayer.motionZ *= speed
        }
    }
}