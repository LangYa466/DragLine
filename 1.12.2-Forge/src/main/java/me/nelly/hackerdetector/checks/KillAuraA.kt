package me.nelly.hackerdetector.checks

import me.nelly.hackerdetector.Category
import me.nelly.hackerdetector.Detection
import net.minecraft.entity.player.EntityPlayer
import kotlin.math.abs

class KillAuraA : Detection("KillAura A", Category.COMBAT) {
    override fun runCheck(player: EntityPlayer): Boolean {
        var hitCount = 0
        var rotationspeed = false
        for (i in player.hurtTime downTo 1) {
            if (abs((player.prevRotationYaw - player.rotationYaw).toDouble()) >= 180) {
                rotationspeed = true
            }
            if (player.attackingEntity != null) {
                hitCount++
            }
        }
        return rotationspeed && hitCount >= 3
    }
}
