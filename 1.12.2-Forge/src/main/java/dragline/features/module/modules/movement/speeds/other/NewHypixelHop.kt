/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.features.module.modules.movement.speeds.other

import dragline.DragLine
import dragline.features.module.modules.movement.Speed
import dragline.features.module.modules.movement.speeds.SpeedMode
import dragline.utils.MovementUtils.isMoving
import dragline.utils.MovementUtils.strafe
import dragline.utils.timer.MSTimer

class NewHypixelHop :
    SpeedMode("HypixelHop") {
    private val timer = MSTimer()
    private var stage = false
    override fun onUpdate() {
        if (!mc2.player.isInWeb && !mc2.player.isInLava && !mc2.player.isInWater && !mc2.player.isOnLadder && mc2.player.ridingEntity == null) {
            val speed = DragLine.moduleManager.getModule(Speed::class.java) as Speed
            if (!mc2.player.isInWeb && !mc2.player.isInLava && !mc2.player.isInWater && !mc2.player.isOnLadder && mc2.player.ridingEntity == null) {
                if (isMoving) {
                    mc2.gameSettings.keyBindJump.pressed = false
                    if (mc2.player.onGround) {
                        mc2.player.jump()
                        mc2.player.speedInAir = 0.02f
                        strafe(0.43f)
                    }
                    if (stage) {
                        if (mc2.player.fallDistance <= 0.1) {
                            mc.timer.timerSpeed = speed.HypixelTimerValue.get()
                        }
                        if (timer.hasTimePassed(650)) {
                            timer.reset()
                            stage = !stage
                        }
                    } else {
                        if (mc2.player.fallDistance > 0.1 && mc2.player.fallDistance < 1.3) {
                            mc.timer.timerSpeed = speed.HypixelDealyTimerValue.get()
                        }
                        if (timer.hasTimePassed(400)) {
                            timer.reset()
                            stage = !stage
                        }
                    }
                    strafe()
                }
            }
        }
    }
}