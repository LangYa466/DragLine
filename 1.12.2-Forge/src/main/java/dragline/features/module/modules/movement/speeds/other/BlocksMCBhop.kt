/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.features.module.modules.movement.speeds.other

import dragline.DragLine
import dragline.features.module.modules.movement.Speed
import dragline.features.module.modules.movement.speeds.SpeedMode
import dragline.utils.MovementUtils

class BlocksMCBhop :
    SpeedMode("BlocksMcBHop") {

    override fun onMotion() {
        if (MovementUtils.isMoving) {
            val speed = DragLine.moduleManager.getModule(Speed::class.java) as Speed? ?: return
            mc.timer.timerSpeed = 1F
            if (mc.thePlayer!!.onGround) {
                MovementUtils.strafe(0.65F)
                mc.thePlayer!!.motionY = 0.2
            } else if (speed.customStrafeValue.get()) MovementUtils.strafe(0.65F) else MovementUtils.strafe()
        } else {
            mc.thePlayer!!.motionZ = 0.0
            mc.thePlayer!!.motionX = mc.thePlayer!!.motionZ
        }

    }

    override fun onDisable() {
        MovementUtils.strafe()
        mc.timer.timerSpeed = 1f
    }
}
