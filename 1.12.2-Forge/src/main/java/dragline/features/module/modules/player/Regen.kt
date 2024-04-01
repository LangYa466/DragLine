/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.player

import dragline.api.minecraft.potion.PotionType
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.MovementUtils
import dragline.value.BoolValue
import dragline.value.IntegerValue
import dragline.value.ListValue

@ModuleInfo(name = "Regen", description = "Regenerates your health much faster.", category = ModuleCategory.PLAYER)
class Regen : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Spartan"), "Vanilla")
    private val healthValue = IntegerValue("Health", 18, 0, 20)
    private val foodValue = IntegerValue("Food", 18, 0, 20)
    private val speedValue = IntegerValue("Speed", 100, 1, 100)
    private val noAirValue = BoolValue("NoAir", false)
    private val potionEffectValue = BoolValue("PotionEffect", false)

    private var resetTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (resetTimer)
            mc.timer.timerSpeed = 1F
        resetTimer = false

        val thePlayer = mc.thePlayer ?: return

        if ((!noAirValue.get() || thePlayer.onGround) && !thePlayer.capabilities.isCreativeMode &&
                thePlayer.foodStats.foodLevel > foodValue.get() && thePlayer.entityAlive && thePlayer.health < healthValue.get()) {
            if (potionEffectValue.get() && !thePlayer.isPotionActive(classProvider.getPotionEnum(PotionType.REGENERATION)))
                return

            when (modeValue.get().toLowerCase()) {
                "vanilla" -> {
                    repeat(speedValue.get()) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(thePlayer.onGround))
                    }
                }

                "spartan" -> {
                    if (MovementUtils.isMoving || !thePlayer.onGround)
                        return

                    repeat(9) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(thePlayer.onGround))
                    }

                    mc.timer.timerSpeed = 0.45F
                    resetTimer = true
                }
            }
        }
    }
}
