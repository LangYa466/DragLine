package me.nelly.hackerdetector.checks

import me.nelly.hackerdetector.Category
import me.nelly.hackerdetector.Detection
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword
import kotlin.math.pow

class ReachA : Detection("Reach A", Category.COMBAT) {
    override fun runCheck(player: EntityPlayer): Boolean {
        val maxAttackDistance = 3.0
        if (player.heldItemMainhand.item is ItemSword) {
            val distanceSq: Double =
                (player.posX - player.lastTickPosX).pow(2.0) + (player.posY - player.lastTickPosY).pow(2.0) + (player.posZ - player.lastTickPosZ).pow(
                    2.0
                )
            return distanceSq > maxAttackDistance.pow(2.0)
        }
        return false
    }
}
