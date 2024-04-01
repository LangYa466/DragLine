package me.nelly.module.world

import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.world.Scaffold
import dragline.utils.MovementUtils
import dragline.utils.RotationUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.ListValue
import me.kid.Scaffold2
import me.rainyfall.module.world.Scaffold3
import nellyobfuscator.NellyClassObfuscator
import sk1d.fdp.module.world.Scaffold4
import sk1d.unknow.module.world.Scaffold5

// By nelly
@NellyClassObfuscator
@ModuleInfo("HytTelly","Nel1y",ModuleCategory.WORLD)
class HytTelly: Module() {

    private val scaffoldModule =
        ListValue("ScaffoldModule", arrayOf("Scaffold", "Scaffold2", "Scaffold3", "Scaffold4","Scaffold5","Scaffold6"), "Scaffold3")
    private val tellymode = ListValue("TellyMode", arrayOf("helical","normal"),"normal")
    private val autoJump = BoolValue("AutoJump", true)
    private val autoTimerValue = BoolValue("AutoTimer", false)
    private val airTimerValue = FloatValue("AirTimer", 0.9f, 0.5f, 5.0f)
    private val groundTimerValue = FloatValue("GroundTimer", 1.0f, 0.5f, 5.0f)
    private val grimsprintValue = BoolValue("GrimSprint", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer!!

        if(tellymode.get().equals("normal")) {
            if (thePlayer.onGround) {
                ground()
            } else {
                air()
            }
        }

        if(tellymode.get().equals("helical")) {
            if (thePlayer.onGround) {
                ground()
            } else {
                air()
            }

            if(isscaffoldChange()) {
                thePlayer.sprinting = false
            } else {
                thePlayer.sprinting = true
            }
        }

        if(grimsprintValue.get()) {
            RotationUtils.targetRotation.pitch = -95F
        }

    }

    private fun ground() {
        val thePlayer = mc.thePlayer!!


        scaffoldChange(false)

        if (autoJump.get() && MovementUtils.isMoving && thePlayer.onGround && !thePlayer.sneaking && !mc.gameSettings.keyBindSneak.isKeyDown && !mc.gameSettings.keyBindJump.isKeyDown &&
            mc.theWorld!!.getCollidingBoundingBoxes(
                thePlayer, thePlayer.entityBoundingBox
                    .offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)
            ).isEmpty()
        ) {
            thePlayer.jump()
        }

        if (autoTimerValue.get()) {
            mc.timer.timerSpeed = groundTimerValue.get()
        }

    }

    private fun air() {
        scaffoldChange(true)

        if (autoTimerValue.get()) {
            mc.timer.timerSpeed = airTimerValue.get()
        }

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

    private fun isscaffoldChange(): Boolean {
        when (scaffoldModule.get().toLowerCase()) {
            "scaffold" -> return DragLine.moduleManager.getModule(Scaffold::class.java).state
            "scaffold2" -> return DragLine.moduleManager.getModule(Scaffold2::class.java).state
            "scaffold3" -> return DragLine.moduleManager.getModule(Scaffold3::class.java).state
            "scaffold4" -> return DragLine.moduleManager.getModule(Scaffold4::class.java).state
            "scaffold5" -> return DragLine.moduleManager.getModule(Scaffold5::class.java).state
            "scaffold6" -> return DragLine.moduleManager.getModule(BlockFly::class.java).state
        }
        return false
    }

    override fun onDisable() {
        scaffoldChange(false)
    }
}