package me.nelly.hackerdetector.checks

import me.nelly.hackerdetector.Category
import me.nelly.hackerdetector.Detection
import net.minecraft.entity.player.EntityPlayer

class VelocityA : Detection("Velocity A", Category.COMBAT) {
    override fun runCheck(player: EntityPlayer): Boolean {
        return if (!player.activePotionEffects.isEmpty()) {
            false
        } else player.hurtResistantTime > 0 && player.velocityChanged && player.motionY >= 0
    }
}
