/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.movement

import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.Render3DEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.MovementUtils
import dragline.utils.block.BlockUtils
import dragline.utils.misc.FallingPlayer
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue
import dragline.value.ListValue
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

@ModuleInfo(name = "BugUp", description = "Automatically setbacks you after falling a certain distance.", category = ModuleCategory.MOVEMENT)
class BugUp : Module() {
    private val modeValue = ListValue("Mode", arrayOf("TeleportBack", "FlyFlag", "OnGroundSpoof", "MotionTeleport-Flag"), "FlyFlag")
    private val maxFallDistance = IntegerValue("MaxFallDistance", 10, 2, 255)
    private val maxDistanceWithoutGround = FloatValue("MaxDistanceToSetback", 2.5f, 1f, 30f)
    private val indicator = BoolValue("Indicator", true)

    private var detectedLocation: WBlockPos? = null
    private var lastFound = 0F
    private var prevX = 0.0
    private var prevY = 0.0
    private var prevZ = 0.0

    override fun onDisable() {
        prevX = 0.0
        prevY = 0.0
        prevZ = 0.0
    }

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        detectedLocation = null

        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.onGround && !classProvider.isBlockAir(
                BlockUtils.getBlock(
                    WBlockPos(thePlayer.posX, thePlayer.posY - 1.0,
                        thePlayer.posZ)
                ))) {
            prevX = thePlayer.prevPosX
            prevY = thePlayer.prevPosY
            prevZ = thePlayer.prevPosZ
        }

        if (!thePlayer.onGround && !thePlayer.isOnLadder && !thePlayer.isInWater) {
            val fallingPlayer = FallingPlayer(
                    thePlayer.posX,
                    thePlayer.posY,
                    thePlayer.posZ,
                    thePlayer.motionX,
                    thePlayer.motionY,
                    thePlayer.motionZ,
                    thePlayer.rotationYaw,
                    thePlayer.moveStrafing,
                    thePlayer.moveForward
            )

            detectedLocation = fallingPlayer.findCollision(60)?.pos

            if (detectedLocation != null && abs(thePlayer.posY - detectedLocation!!.y) +
                    thePlayer.fallDistance <= maxFallDistance.get()) {
                lastFound = thePlayer.fallDistance
            }

            if (thePlayer.fallDistance - lastFound > maxDistanceWithoutGround.get()) {
                val mode = modeValue.get()

                when (mode.toLowerCase()) {
                    "teleportback" -> {
                        thePlayer.setPositionAndUpdate(prevX, prevY, prevZ)
                        thePlayer.fallDistance = 0F
                        thePlayer.motionY = 0.0
                    }
                    "flyflag" -> {
                        thePlayer.motionY += 0.1
                        thePlayer.fallDistance = 0F
                    }
                    "ongroundspoof" -> mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(true))

                    "motionteleport-flag" -> {
                        thePlayer.setPositionAndUpdate(thePlayer.posX, thePlayer.posY + 1f, thePlayer.posZ)
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, true))
                        thePlayer.motionY = 0.1

                        MovementUtils.strafe()
                        thePlayer.fallDistance = 0f
                    }
                }
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (detectedLocation == null || !indicator.get() ||
                thePlayer.fallDistance + (thePlayer.posY - (detectedLocation!!.y + 1)) < 3)
            return

        val x = detectedLocation!!.x
        val y = detectedLocation!!.y
        val z = detectedLocation!!.z

        val renderManager = mc.renderManager

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glLineWidth(2f)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        RenderUtils.glColor(Color(255, 0, 0, 90))
        RenderUtils.drawFilledBox(classProvider.createAxisAlignedBB(
                x - renderManager.renderPosX,
                y + 1 - renderManager.renderPosY,
                z - renderManager.renderPosZ,
                x - renderManager.renderPosX + 1.0,
                y + 1.2 - renderManager.renderPosY,
                z - renderManager.renderPosZ + 1.0)
        )

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)

        val fallDist = floor(thePlayer.fallDistance + (thePlayer.posY - (y + 0.5))).toInt()

        RenderUtils.renderNameTag("${fallDist}m (~${max(0, fallDist - 3)} damage)", x + 0.5, y + 1.7, z + 0.5)

        classProvider.getGlStateManager().resetColor()
    }
}