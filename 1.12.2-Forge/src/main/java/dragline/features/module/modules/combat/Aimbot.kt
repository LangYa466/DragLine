/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.combat

import dragline.event.EventTarget
import dragline.event.StrafeEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.EntityUtils
import dragline.utils.Rotation
import dragline.utils.RotationUtils
import dragline.utils.extensions.getDistanceToEntityBox
import dragline.utils.misc.RandomUtils
import dragline.utils.timer.MSTimer
import dragline.value.BoolValue
import dragline.value.FloatValue
import kotlin.random.Random

@ModuleInfo(name = "Aimbot", description = "Automatically faces selected entities around you.", category = ModuleCategory.COMBAT)
class Aimbot : Module() {

    private val rangeValue = FloatValue("Range", 4.4F, 1F, 8F)
    private val turnSpeedValue = FloatValue("TurnSpeed", 2F, 1F, 180F)
    private val fovValue = FloatValue("FOV", 180F, 1F, 180F)
    private val centerValue = BoolValue("Center", false)
    private val lockValue = BoolValue("Lock", true)
    private val onClickValue = BoolValue("OnClick", false)
    private val jitterValue = BoolValue("Jitter", false)

    private val clickTimer = MSTimer()

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (mc.gameSettings.keyBindAttack.isKeyDown)
            clickTimer.reset()

        if (onClickValue.get() && clickTimer.hasTimePassed(500L))
            return

        val thePlayer = mc.thePlayer ?: return

        val range = rangeValue.get()
        val entity = mc.theWorld!!.loadedEntityList
                .filter {
                    EntityUtils.isSelected(it, true) && thePlayer.canEntityBeSeen(it) &&
                            thePlayer.getDistanceToEntityBox(it) <= range && RotationUtils.getRotationDifference(it) <= fovValue.get()
                }
                .minBy { RotationUtils.getRotationDifference(it) } ?: return

        if (!lockValue.get() && RotationUtils.isFaced(entity, range.toDouble()))
            return

        val rotation = RotationUtils.limitAngleChange(
                Rotation(thePlayer.rotationYaw, thePlayer.rotationPitch),
                if (centerValue.get())
                    RotationUtils.toRotation(RotationUtils.getCenter(entity.entityBoundingBox), true)
                else
                    RotationUtils.searchCenter(entity.entityBoundingBox, false, false, true,
                            false, range).rotation,
                (turnSpeedValue.get() + Math.random()).toFloat()
        )

        rotation.toPlayer(thePlayer)

        if (jitterValue.get()) {
            val yaw = Random.nextBoolean()
            val pitch = Random.nextBoolean()
            val yawNegative = Random.nextBoolean()
            val pitchNegative = Random.nextBoolean()

            if (yaw)
                thePlayer.rotationYaw += if (yawNegative) -RandomUtils.nextFloat(0F, 1F) else RandomUtils.nextFloat(0F, 1F)

            if (pitch) {
                thePlayer.rotationPitch += if (pitchNegative) -RandomUtils.nextFloat(0F, 1F) else RandomUtils.nextFloat(0F, 1F)
                if (thePlayer.rotationPitch > 90)
                    thePlayer.rotationPitch = 90F
                else if (thePlayer.rotationPitch < -90)
                    thePlayer.rotationPitch = -90F
            }
        }
    }
}