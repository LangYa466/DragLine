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

@ModuleInfo(name = "NoClip", description = "Allows you to freely move through walls (A sandblock has to fall on your head).", category = ModuleCategory.MOVEMENT)
class NoClip : Module() {

    override fun onDisable() {
        mc.thePlayer?.noClip = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        thePlayer.noClip = true
        thePlayer.fallDistance = 0f
        thePlayer.onGround = false

        thePlayer.capabilities.isFlying = false
        thePlayer.motionX = 0.0
        thePlayer.motionY = 0.0
        thePlayer.motionZ = 0.0

        val speed = 0.32f

        thePlayer.jumpMovementFactor = speed

        if (mc.gameSettings.keyBindJump.isKeyDown)
            thePlayer.motionY += speed.toDouble()

        if (mc.gameSettings.keyBindSneak.isKeyDown)
            thePlayer.motionY -= speed.toDouble()
    }
}
