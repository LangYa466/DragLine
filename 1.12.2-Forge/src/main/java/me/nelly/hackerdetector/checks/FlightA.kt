package me.nelly.hackerdetector.checks

import me.nelly.hackerdetector.Category
import me.nelly.hackerdetector.Detection
import me.nelly.hackerdetector.utils.MovementUtils
import net.minecraft.entity.player.EntityPlayer

class FlightA : Detection("Flight A", Category.MOVEMENT) {
    override fun runCheck(player: EntityPlayer): Boolean {
        return !player.onGround && player.motionY == 0.0 && MovementUtils.isMoving(player)
    }
}
