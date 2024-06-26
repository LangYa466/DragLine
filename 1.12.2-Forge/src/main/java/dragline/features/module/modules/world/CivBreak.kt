/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.world

import dragline.api.minecraft.network.play.client.ICPacketPlayerDigging
import dragline.api.minecraft.util.IEnumFacing
import dragline.api.minecraft.util.WBlockPos
import dragline.event.*
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.RotationUtils
import dragline.utils.block.BlockUtils
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import java.awt.Color

@ModuleInfo(name = "CivBreak", description = "Allows you to break blocks instantly.", category = ModuleCategory.WORLD)
class CivBreak : Module() {

    private var blockPos: WBlockPos? = null
    private var enumFacing: IEnumFacing? = null

    private val range = FloatValue("Range", 5F, 1F, 6F)
    private val rotationsValue = BoolValue("Rotations", true)
    private val visualSwingValue = BoolValue("VisualSwing", true)

    private val airResetValue = BoolValue("Air-Reset", true)
    private val rangeResetValue = BoolValue("Range-Reset", true)


    @EventTarget
    fun onBlockClick(event: ClickBlockEvent) {
        if (classProvider.isBlockBedrock(event.clickedBlock?.let { BlockUtils.getBlock(it) }))
            return

        blockPos = event.clickedBlock ?: return
        enumFacing = event.WEnumFacing ?: return

        // Break
        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.START_DESTROY_BLOCK, blockPos!!, enumFacing!!))
        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK, blockPos!!, enumFacing!!))
    }

    @EventTarget
    fun onUpdate(event: MotionEvent) {
        val pos = blockPos ?: return

        if (airResetValue.get() && classProvider.isBlockAir(BlockUtils.getBlock(pos)) ||
                rangeResetValue.get() && BlockUtils.getCenterDistance(pos) > range.get()) {
            blockPos = null
            return
        }

        if (classProvider.isBlockAir(BlockUtils.getBlock(pos)) || BlockUtils.getCenterDistance(pos) > range.get())
            return

        when (event.eventState) {
            EventState.PRE -> if (rotationsValue.get())
                RotationUtils.setTargetRotation((RotationUtils.faceBlock(pos) ?: return).rotation)

            EventState.POST -> {
                if (visualSwingValue.get())
                    mc.thePlayer!!.swingItem()
                else
                    mc.netHandler.addToSendQueue(classProvider.createCPacketAnimation())

                // Break
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(
                    ICPacketPlayerDigging.WAction.START_DESTROY_BLOCK,
                        blockPos!!, enumFacing!!))
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(
                    ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK,
                        blockPos!!, enumFacing!!))
                mc.playerController.clickBlock(blockPos!!, enumFacing!!)
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawBlockBox(blockPos ?: return, Color.RED, true)
    }
}