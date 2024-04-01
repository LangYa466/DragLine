/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.movement

import dragline.DragLine
import dragline.api.minecraft.network.play.client.ICPacketPlayerPosition
import dragline.api.minecraft.util.IAxisAlignedBB
import dragline.event.*
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.ui.client.hud.element.elements.Notification
import dragline.ui.client.hud.element.elements.NotifyType
import dragline.utils.MovementUtils
import dragline.utils.timer.MSTimer
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.ListValue
import net.minecraft.network.play.client.CPacketPlayer
import org.lwjgl.input.Keyboard

@ModuleInfo(name = "Fly", description = "Allows you to fly in survival mode.", category = ModuleCategory.MOVEMENT, keyBind = Keyboard.KEY_F)
class Fly : Module() {
    val modeValue = ListValue("Mode", arrayOf(
            "Vanilla",
            "SmoothVanilla",

            // GrimAntiCheat
            "GrimTNT"

    ), "Vanilla")
    private val vanillaSpeedValue = FloatValue("VanillaSpeed", 2f, 0f, 5f)
    private val vanillaKickBypassValue = BoolValue("VanillaKickBypass", false)


    // Visuals
    private val flyTimer = MSTimer()
    private val groundTimer = MSTimer()


    override fun onEnable() {
        val thePlayer = mc.thePlayer ?: return

        flyTimer.reset()

        val mode = modeValue.get()

        if(mode.equals("GrimTNT")) {
            if(mc2.isSingleplayer) {
                DragLine.hud.addNotification(Notification("Fly","GrimTNT Can only be enabled in single-player worlds",NotifyType.ERROR))
                state = false
            }
        }


        super.onEnable()
    }

    override fun onDisable() {

        val thePlayer = mc.thePlayer ?: return

        thePlayer.capabilities.isFlying = false
        mc.timer.timerSpeed = 1f
        thePlayer.speedInAir = 0.02f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val vanillaSpeed = vanillaSpeedValue.get()
        val thePlayer = mc.thePlayer!!

        // 你为什么要写一个run
        when (modeValue.get().toLowerCase()) {
            "grimtnt" -> {
                mc.thePlayer!!.setPositionAndRotation(
                    mc.thePlayer!!.posX + 1000,
                    mc.thePlayer!!.posY,
                    mc.thePlayer!!.posZ + 1000,
                    mc.thePlayer!!.rotationYaw,
                    mc.thePlayer!!.rotationPitch
                )
            }

            "vanilla" -> {
                thePlayer.capabilities.isFlying = false
                thePlayer.motionY = 0.0
                thePlayer.motionX = 0.0
                thePlayer.motionZ = 0.0
                if (mc.gameSettings.keyBindJump.isKeyDown) thePlayer.motionY += vanillaSpeed
                if (mc.gameSettings.keyBindSneak.isKeyDown) thePlayer.motionY -= vanillaSpeed
                MovementUtils.strafe(vanillaSpeed)
                handleVanillaKickBypass()
            }

            "smoothvanilla" -> {
                thePlayer.capabilities.isFlying = true
                handleVanillaKickBypass()
            }
        }
    }

    // Sk1d For LBPlus
    private fun handleVanillaKickBypass() {
        if (!vanillaKickBypassValue.get() || !groundTimer.hasTimePassed(1000)) return
        val ground = calculateGround()
        run {
            var posY = mc.thePlayer!!.posY
            while (posY > ground) {
                mc2.connection!!.sendPacket((CPacketPlayer.Position(mc.thePlayer!!.posX, posY, mc.thePlayer!!.posZ, true)))
                if (posY - 8.0 < ground) break // Prevent next step
                posY -= 8.0
            }
        }
        mc2.connection!!.sendPacket(CPacketPlayer.Position(mc.thePlayer!!.posX, ground, mc.thePlayer!!.posZ, true))
        var posY = ground
        while (posY < mc.thePlayer!!.posY) {
            mc2.connection!!.sendPacket((CPacketPlayer.Position(mc.thePlayer!!.posX, posY, mc.thePlayer!!.posZ, true)))
            if (posY + 8.0 > mc.thePlayer!!.posY) break // Prevent next step
            posY += 8.0
        }

        mc2.connection!!.sendPacket(
                CPacketPlayer.Position(
                mc.thePlayer!!.posX,
                mc.thePlayer!!.posY,
                mc.thePlayer!!.posZ,
                true
            )
        )

        groundTimer.reset()
    }

    // TODO: Make better and faster calculation lol
    private fun calculateGround(): Double {
        val playerBoundingBox: IAxisAlignedBB = mc.thePlayer!!.entityBoundingBox
        var blockHeight = 1.0
        var ground = mc.thePlayer!!.posY
        while (ground > 0.0) {
            val customBox = classProvider.createAxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ)
            if (mc.theWorld!!.checkBlockCollision(customBox)) {
                if (blockHeight <= 0.05) return ground + blockHeight
                ground += blockHeight
                blockHeight = 0.05
            }
            ground -= blockHeight
        }
        return 0.0
    }

    override val tag: String
        get() = modeValue.get()
}