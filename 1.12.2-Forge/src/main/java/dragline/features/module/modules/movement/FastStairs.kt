/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.movement

import dragline.DragLine
import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.MovementUtils
import dragline.utils.block.BlockUtils.getBlock
import dragline.value.BoolValue
import dragline.value.ListValue

@ModuleInfo(name = "FastStairs", description = "Allows you to climb up stairs faster.", category = ModuleCategory.MOVEMENT)
class FastStairs : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Step", "NCP", "AAC3.1.0", "AAC3.3.6", "AAC3.3.13"), "NCP")
    private val longJumpValue = BoolValue("LongJump", false)

    private var canJump = false

    private var walkingDown = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (!MovementUtils.isMoving || DragLine.moduleManager[Speed::class.java]!!.state)
            return

        if (thePlayer.fallDistance > 0 && !walkingDown)
            walkingDown = true
        else if (thePlayer.posY > thePlayer.prevChasingPosY)
            walkingDown = false

        val mode = modeValue.get()

        if (!thePlayer.onGround)
            return

        val blockPos = WBlockPos(thePlayer.posX, thePlayer.entityBoundingBox.minY, thePlayer.posZ)

        if (classProvider.isBlockStairs(getBlock(blockPos)) && !walkingDown) {
            thePlayer.setPosition(thePlayer.posX, thePlayer.posY + 0.5, thePlayer.posZ)

            val motion = when {
                mode.equals("NCP", ignoreCase = true) -> 1.4
                mode.equals("AAC3.1.0", ignoreCase = true) -> 1.5
                mode.equals("AAC3.3.13", ignoreCase = true) -> 1.2
                else -> 1.0
            }

            thePlayer.motionX *= motion
            thePlayer.motionZ *= motion
        }

        if (classProvider.isBlockStairs(getBlock(blockPos.down()))) {
            if (walkingDown) {
                when {
                    mode.equals("NCP", ignoreCase = true) ->
                        thePlayer.motionY = -1.0
                    mode.equals("AAC3.3.13", ignoreCase = true) ->
                        thePlayer.motionY -= 0.014
                }

                return
            }

            val motion = when {
                mode.equals("NCP", ignoreCase = true) -> 1.3
                mode.equals("AAC3.1.0", ignoreCase = true) -> 1.3
                mode.equals("AAC3.3.6", ignoreCase = true) -> 1.48
                mode.equals("AAC3.3.13", ignoreCase = true) -> 1.52
                else -> 1.3
            }

            thePlayer.motionX *= motion
            thePlayer.motionZ *= motion
            canJump = true
        } else if (mode.startsWith("AAC", ignoreCase = true) && canJump) {
            if (longJumpValue.get()) {
                thePlayer.jump()
                thePlayer.motionX *= 1.35
                thePlayer.motionZ *= 1.35
            }

            canJump = false
        }
    }

    override val tag: String
        get() = modeValue.get()
}