package me.rainyfall.module.world

import dragline.DragLine
import dragline.api.enums.BlockType
import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.TickEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.render.NoFOV
import dragline.features.module.modules.world.Scaffold
import dragline.features.module.modules.world.Timer
import dragline.utils.MovementUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.ListValue
import me.kid.Scaffold2
import me.nelly.module.world.BlockFly
import sk1d.fdp.module.world.Scaffold4
import sk1d.unknow.module.world.Scaffold5
import kotlin.jvm.internal.Intrinsics

@ModuleInfo(name = "HytTelly", description = "form rainyfall", category = ModuleCategory.WORLD)
class GroundTelly : Module() {
    private val scaffoldModule =
        ListValue("ScaffoldModule", arrayOf("Scaffold", "Scaffold2", "Scaffold3", "Scaffold4","Scaffold5","Scaffold6"), "Scaffold")
    private val autoJumpValue = BoolValue("AutoJump", false)
    private val autoJumpHelper =
        ListValue("JumpHelper", arrayOf("Parkour", "Eagle", "Test"), "Parkour").displayable { autoJumpValue.get() }
    private val autoJumpMode = ListValue(
        "AutoJumpMode", arrayOf(
            "MCInstanceJump",
            "MCInstance2Jump",
            "ClientMotionY"
        ), "MCInstanceJump"
    ).displayable { autoJumpValue.get() }
    private val eventTargetSelector = ListValue(
        "EventSelect", arrayOf(
            "onUpdate",
            "onTick"
        ), "onUpdate"
    )

    private val noBobValue = BoolValue("NoBob", false)

    companion object {
        @JvmStatic
        val noFovValue = BoolValue("NoFov", false)

    }

    private val autoTimerValue = BoolValue("AutoTimer", false)
    private val autoPitchValue = BoolValue("setBestPitch", false)
    private val alwaysPitchValue = BoolValue("setPitch-onUpdate", false).displayable { autoPitchValue.get() }
    private val customPitchValue = FloatValue("CustomPitch",26.5F,0F,90F)
    private val autoYawValue = ListValue("setYawMode", arrayOf("None", "onEnable", "onUpdate"), "None")
    private val disableAllOnEnable = BoolValue("Enable-DisableAll", false)
    private val disableAllOnDisable = BoolValue("Disable-DisableAll", false)

    override fun onEnable() {
        if (autoPitchValue.get()) {
            mc.thePlayer!!.rotationPitch = customPitchValue.get()
        }
        if (autoYawValue.get().equals("onEnable")) setYaw()

        if (disableAllOnEnable.get()) disableAll()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer!!

        if (autoPitchValue.get() && alwaysPitchValue.get()) {
            mc.thePlayer!!.rotationPitch = customPitchValue.get()
        }
        if (autoYawValue.get().equals("onUpdate")) setYaw()
        if (noBobValue.get()) mc.thePlayer!!.distanceWalkedModified = 0f
        if (!thePlayer.sneaking) {
            val thePlayer2 = mc.thePlayer
            if (thePlayer2 == null) {
                Intrinsics.throwNpe()
            }
            if (thePlayer2!!.onGround) {
                scaffoldChange(false)
                if (autoTimerValue.get()) if (DragLine.moduleManager.getModule(Timer::class.java).state) DragLine.moduleManager.getModule(
                    Timer::class.java
                ).state = false
            } else {
                scaffoldChange(true)
                if (autoTimerValue.get()) if (!DragLine.moduleManager.getModule(Timer::class.java).state) DragLine.moduleManager.getModule(
                    Timer::class.java
                ).state = true
            }
        }
        if (autoJumpValue.get() && eventTargetSelector.get().equals("onUpdate", true)) tryJump()
    }

    private fun jump() {
        when (autoJumpMode.get().toLowerCase()) {
            "mcinstancejump" -> mc.thePlayer!!.jump()
            "mcinstance2jump" -> mc2.player.jump()
            "clientmotiony" -> mc.thePlayer!!.motionY = 0.42
        }
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        if (autoJumpValue.get() && eventTargetSelector.get().equals("onTick", true)) tryJump()
    }

    @EventTarget
    override fun onDisable() {
        scaffoldChange(false)
        if (autoTimerValue.get()) if (DragLine.moduleManager.getModule(Timer::class.java).state) DragLine.moduleManager.getModule(Timer::class.java).state =
            false
        if (disableAllOnDisable.get()) disableAll()
    }

    private fun scaffoldChange(state: Boolean) {
        when (scaffoldModule.get().toLowerCase()) {
            "scaffold" -> DragLine.moduleManager.getModule(Scaffold::class.java).state = state
            "scaffold2" -> DragLine.moduleManager.getModule(Scaffold2::class.java).state = state
            "scaffold3" -> DragLine.moduleManager.getModule(Scaffold3::class.java).state = state
            "scaffold4" -> DragLine.moduleManager.getModule(Scaffold4::class.java).state = state
            "scaffold5" -> DragLine.moduleManager.getModule(Scaffold5::class.java).state = state
            "scaffold6" -> DragLine.moduleManager.getModule(BlockFly::class.java).state = state
        }
    }

    private fun tryJump() {
        val thePlayer = mc.thePlayer!!
        when (autoJumpHelper.get().toLowerCase()) {
            "parkour" -> if (MovementUtils.isMoving && thePlayer.onGround && !thePlayer.sneaking && !mc.gameSettings.keyBindSneak.isKeyDown && !mc.gameSettings.keyBindJump.isKeyDown &&
                mc.theWorld!!.getCollidingBoundingBoxes(
                    thePlayer, thePlayer.entityBoundingBox
                        .offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)
                ).isEmpty()
            ) {
                jump()
            }

            "eagle" -> {
                if (mc.theWorld!!.getBlockState(
                        WBlockPos(
                            thePlayer.posX,
                            thePlayer.posY - 1.0,
                            thePlayer.posZ
                        )
                    ).block == classProvider.getBlockEnum(
                        BlockType.AIR
                    ) && thePlayer.onGround
                ) jump()
            }

            "test" -> {
                if (thePlayer.onGround && MovementUtils.isMoving && thePlayer.sprinting) {
                    jump()
                }
            }
        }
    }

    private fun disableAll() {
        DragLine.moduleManager.getModule(Scaffold::class.java).state = false
        DragLine.moduleManager.getModule(Scaffold2::class.java).state = false
        DragLine.moduleManager.getModule(Scaffold3::class.java).state = false
        DragLine.moduleManager.getModule(Scaffold4::class.java).state = false
    }

    private fun setYaw() {
        val thePlayer = mc.thePlayer!!
        if (autoYawValue.get().toLowerCase().equals("none")) return
        val x = java.lang.Double.valueOf(thePlayer.motionX)
        val y = java.lang.Double.valueOf(thePlayer.motionZ)
        if (mc.gameSettings.keyBindForward.isKeyDown) {
            if (y != null &&
                y.toDouble() > 0.1
            ) {
                thePlayer.rotationYaw = 0.0f
            }
            if (y != null &&
                y.toDouble() < -0.1
            ) {
                thePlayer.rotationYaw = 180.0f
            }
            if (x != null &&
                x.toDouble() > 0.1
            ) {
                thePlayer.rotationYaw = -90.0f
            }
            if (x != null &&
                x.toDouble() < -0.1
            ) {
                thePlayer.rotationYaw = 90.0f
            }
        }

    }
}
