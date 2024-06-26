/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.features.module.modules.combat

import dragline.event.EventTarget
import dragline.event.Render3DEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.misc.RandomUtils
import dragline.utils.timer.TimeUtils
import dragline.value.BoolValue
import dragline.value.IntegerValue
import kotlin.random.Random

@ModuleInfo(name = "AutoClicker", description = "Constantly clicks when holding down a mouse button.", category = ModuleCategory.COMBAT)
class AutoClicker : Module() {
    private val maxCPSValue: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {

        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minCPSValue.get()
            if (minCPS > newValue)
                set(minCPS)
        }

    }

    private val minCPSValue: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {

        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxCPS = maxCPSValue.get()
            if (maxCPS < newValue)
                set(maxCPS)
        }

    }

    private val rightValue = BoolValue("Right", true)
    private val leftValue = BoolValue("Left", true)
    private val jitterValue = BoolValue("Jitter", false)
    private val breakBlockValue = BoolValue("LeftBreakBlocks",false).displayable { leftValue.get() }
    private val onlyBlockValue = BoolValue("RightOnlyBlocks",false).displayable { rightValue.get() }

    private var rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var rightLastSwing = 0L
    private var leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var leftLastSwing = 0L

    @EventTarget
    fun onRender(event: Render3DEvent) {
        // Left click
        if (mc.gameSettings.keyBindAttack.isKeyDown && leftValue.get() &&
            System.currentTimeMillis() - leftLastSwing >= leftDelay && mc.playerController.curBlockDamageMP == 0F) {

            if (!breakBlockValue.get() || mc2.playerController.isHittingBlock) mc.gameSettings.keyBindAttack.onTick(mc.gameSettings.keyBindAttack.keyCode) // Minecraft Click Handling

            leftLastSwing = System.currentTimeMillis()
            leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
        }

        // Right click
        if (mc.gameSettings.keyBindUseItem.isKeyDown && !mc.thePlayer!!.isUsingItem && rightValue.get() &&
            System.currentTimeMillis() - rightLastSwing >= rightDelay) {
            if (!onlyBlockValue.get() || classProvider.isItemBlock(mc.thePlayer!!.heldItem?.item)) mc.gameSettings.keyBindAttack.onTick(mc.gameSettings.keyBindUseItem.keyCode) // Minecraft Click Handling
            rightLastSwing = System.currentTimeMillis()
            rightDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (jitterValue.get() && (leftValue.get() && mc.gameSettings.keyBindAttack.isKeyDown && mc.playerController.curBlockDamageMP == 0F
                    || rightValue.get() && mc.gameSettings.keyBindUseItem.isKeyDown && !thePlayer.isUsingItem)) {
            if (Random.nextBoolean()) thePlayer.rotationYaw += if (Random.nextBoolean()) -RandomUtils.nextFloat(0F, 1F) else RandomUtils.nextFloat(0F, 1F)

            if (Random.nextBoolean()) {
                thePlayer.rotationPitch += if (Random.nextBoolean()) -RandomUtils.nextFloat(
                    0F,
                    1F
                ) else RandomUtils.nextFloat(0F, 1F)

                // Make sure pitch is not going into unlegit values
                if (thePlayer.rotationPitch > 90)
                    thePlayer.rotationPitch = 90F
                else if (thePlayer.rotationPitch < -90)
                    thePlayer.rotationPitch = -90F
            }
        }
    }
}