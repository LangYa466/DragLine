/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package dragline.features.module.modules.movement

import dragline.event.EventTarget
import dragline.event.JumpEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.MovementUtils
import dragline.value.FloatValue
import dragline.value.ListValue

@ModuleInfo(name = "NoWeb", description = "Sk1d Form FDPCLIENT", category = ModuleCategory.MOVEMENT)
class NoWeb : Module() {

    private val modeValue = ListValue("Mode", arrayOf("None", "FastFall", "OldAAC", "LAAC", "Rewinside", "Horizon", "Spartan", "AAC4", "AAC5", "Matrix", "Test", "IntaveTest"), "None")
    private val horizonSpeed = FloatValue("HorizonSpeed", 0.1F, 0.01F, 0.8F)

    private var usedTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F
            usedTimer = false
        }
        if (!mc.thePlayer!!.isInWeb) {
            return
        }

        when (modeValue.get().toLowerCase()) {
            "none" -> mc.thePlayer!!.isInWeb = false
            "fastfall" -> {
                //Bypass AAC(All) Vulcan Verus Matrix NCP3.17 HAWK Spartan
                if (mc.thePlayer!!.onGround) mc.thePlayer!!.jump();
                if (mc.thePlayer!!.motionY > 0f) {
                    mc.thePlayer!!.motionY -= mc.thePlayer!!.motionY * 2
                }
            }
            "oldaac" -> {
                mc.thePlayer!!.jumpMovementFactor = 0.59f

                if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                    mc.thePlayer!!.motionY = 0.0
                }
            }
            "laac" -> {
                mc.thePlayer!!.jumpMovementFactor = if (mc.thePlayer!!.movementInput.moveStrafe != 0f) 1.0f else 1.21f

                if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                    mc.thePlayer!!.motionY = 0.0
                }

                if (mc.thePlayer!!.onGround) {
                    mc.thePlayer!!.jump()
                }
            }
            "aac4" -> {
                mc.timer.timerSpeed = 0.99F
                mc.thePlayer!!.jumpMovementFactor = 0.02958f
                mc.thePlayer!!.motionY -= 0.00775
                if (mc.thePlayer!!.onGround) {
                    // mc.thePlayer!!.jump()
                    mc.thePlayer!!.motionY = 0.4050
                    mc.timer.timerSpeed = 1.35F
                }
            }
            "horizon" -> {
                if (mc.thePlayer!!.onGround) {
                    MovementUtils.strafe(horizonSpeed.get())
                }
            }
            "spartan" -> {
                MovementUtils.strafe(0.27F)
                mc.timer.timerSpeed = 3.7F
                if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                    mc.thePlayer!!.motionY = 0.0
                }
                if (mc.thePlayer!!.ticksExisted % 2 == 0) {
                    mc.timer.timerSpeed = 1.7F
                }
                if (mc.thePlayer!!.ticksExisted % 40 == 0) {
                    mc.timer.timerSpeed = 3F
                }
                usedTimer = true
            }
            "matrix" -> {
                mc.thePlayer!!.jumpMovementFactor = 0.12425f
                mc.thePlayer!!.motionY = -0.0125
                if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer!!.motionY = -0.1625

                if (mc.thePlayer!!.ticksExisted % 40 == 0) {
                    mc.timer.timerSpeed = 3.0F
                    usedTimer = true
                }
            }
            "aac5" -> {
                mc.thePlayer!!.jumpMovementFactor = 0.42f

                if (mc.thePlayer!!.onGround) {
                    mc.thePlayer!!.jump()
                }
            }
            "test" -> {
                if (mc.thePlayer!!.ticksExisted % 7 == 0) {
                    mc.thePlayer!!.jumpMovementFactor = 0.42f
                }
                if (mc.thePlayer!!.ticksExisted % 7 == 1) {
                    mc.thePlayer!!.jumpMovementFactor = 0.33f
                }
                if (mc.thePlayer!!.ticksExisted % 7 == 2) {
                    mc.thePlayer!!.jumpMovementFactor = 0.08f
                }
            }
            "intavetest" -> {
                if (mc.thePlayer!!.movementInput.moveStrafe == 0.0F && mc.gameSettings.keyBindForward.isKeyDown && mc.thePlayer!!.isCollidedVertically) {
                    mc.thePlayer!!.jumpMovementFactor = 0.74F
                } else {
                    mc.thePlayer!!.jumpMovementFactor = 0.2F
                    mc.thePlayer!!.onGround = true
                }
            }
            "rewinside" -> {
                mc.thePlayer!!.jumpMovementFactor = 0.42f

                if (mc.thePlayer!!.onGround) {
                    mc.thePlayer!!.jump()
                }
            }
        }
    }
    fun onJump(event: JumpEvent) {
        if (modeValue.equals("AAC4")) {
            event.cancelEvent()
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0F
    }

    override val tag: String?
        get() = modeValue.get()
}