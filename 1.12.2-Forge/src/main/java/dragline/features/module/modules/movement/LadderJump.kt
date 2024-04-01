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

@ModuleInfo(name = "LadderJump", description = "Boosts you up when touching a ladder.", category = ModuleCategory.MOVEMENT)
class LadderJump : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.onGround) {
            if (thePlayer.isOnLadder) {
                thePlayer.motionY = 1.5
                jumped = true
            } else jumped = false
        } else if (!thePlayer.isOnLadder && jumped) thePlayer.motionY += 0.059
    }

    companion object {
        @JvmField
        var jumped = false
    }
}