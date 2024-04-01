package dragline.features.module.modules.movement.speeds.other

import dragline.event.MoveEvent
import dragline.features.module.modules.movement.speeds.SpeedMode
import dragline.utils.MovementUtils

class HytSlowHop :
    SpeedMode("HytSlowHop") {
    override fun onMotion() {
    }

    override fun onUpdate() {
        mc.gameSettings.keyBindJump.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)
        if (MovementUtils.isMoving) {
            if (mc.thePlayer!!.onGround) {
                mc.gameSettings.keyBindJump.pressed = false
                mc.thePlayer!!.jump()
            }

            if (mc.thePlayer!!.motionY > 0.003) {
                mc.thePlayer!!.motionX *= 1.00015
                mc.thePlayer!!.motionZ *= 1.00015
            }
        }

    }

    override fun onMove(event: MoveEvent) {}
}