package dragline.features.module.modules.movement.speeds.other


import dragline.features.module.modules.movement.speeds.SpeedMode
import dragline.utils.MovementUtils
import kotlin.math.abs

class VulcanHop :
    SpeedMode("VulcanHop") {

    override fun onUpdate() {
        mc.timer.timerSpeed = 1.00f
        if (abs(mc.thePlayer!!.movementInput.moveStrafe) < 0.1f) {
            mc.thePlayer!!.jumpMovementFactor = 0.0265f
        } else {
            mc.thePlayer!!.jumpMovementFactor = 0.0244f
        }
        if (!mc.thePlayer!!.onGround) {
            mc.gameSettings.keyBindJump.pressed = mc.gameSettings.keyBindJump.isKeyDown
        }
        if (MovementUtils.speed < 0.215f) {
            MovementUtils.strafe(0.215f)
        }
        if (mc.thePlayer!!.onGround && MovementUtils.isMoving) {
            mc.timer.timerSpeed = 1.25f
            mc.gameSettings.keyBindJump.pressed = false
            mc.thePlayer!!.jump()
            MovementUtils.strafe()
            if (MovementUtils.speed < 0.5f) {
                MovementUtils.strafe(0.4849f)
            }
        } else if (!MovementUtils.isMoving) {
            mc.timer.timerSpeed = 1.00f
            mc.thePlayer!!.motionX = 0.0
            mc.thePlayer!!.motionZ = 0.0
        }
    }
}