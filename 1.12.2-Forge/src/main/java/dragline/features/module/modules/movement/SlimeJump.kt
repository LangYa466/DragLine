/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.movement

import dragline.event.EventTarget
import dragline.event.JumpEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.block.BlockUtils.getBlock
import dragline.value.FloatValue
import dragline.value.ListValue

@ModuleInfo(name = "SlimeJump", description = "Allows you to to jump higher on slime blocks.", category = ModuleCategory.MOVEMENT)
class SlimeJump : Module() {
    private val motionValue = FloatValue("Motion", 0.42f, 0.2f, 1f)
    private val modeValue = ListValue("Mode", arrayOf("Set", "Add"), "Add")

    @EventTarget
    fun onJump(event: JumpEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (mc.thePlayer != null && mc.theWorld != null && classProvider.isBlockSlime(getBlock(thePlayer.position.down()))) {
            event.cancelEvent()

            when (modeValue.get().toLowerCase()) {
                "set" -> thePlayer.motionY = motionValue.get().toDouble()
                "add" -> thePlayer.motionY += motionValue.get()
            }
        }
    }
}