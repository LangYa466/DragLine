package dragline.features.module.modules.movement.speeds.other


import dragline.event.MoveEvent
import dragline.features.module.modules.movement.speeds.SpeedMode
import dragline.utils.MovementUtils
import dragline.utils.misc.MiscUtils.movingForward
import dragline.utils.timer.MSTimer

class Legit : SpeedMode("Legit") {
    val timer = MSTimer()
    override fun onUpdate() {
        if (!MovementUtils.isMoving)
            return
        if (!movingForward()) {
            mc2.player.isSprinting = false
            mc.thePlayer!!.serverSprintState = false
        }
        if (!mc2.player.isInWeb
            && !mc2.player.isInLava
            && !mc2.player.isInWater
            && !mc2.player.isOnLadder
            && mc2.player.ridingEntity == null
            && mc2.player.onGround
            && !mc2.player.isDead
            && movingForward()
            && timer.hasTimePassed(550)
        ) {
            mc.thePlayer!!.jump()
            timer.reset()
        }
    }

    override fun onMove(event: MoveEvent?) {
        super.onMove(event)
    }
}
