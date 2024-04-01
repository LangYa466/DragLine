/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.world

import dragline.DragLine
import dragline.api.enums.BlockType
import dragline.api.enums.EnumFacingType
import dragline.api.minecraft.util.WBlockPos
import dragline.api.minecraft.util.WVec3
import dragline.event.EventState
import dragline.event.EventTarget
import dragline.event.MotionEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.player.Blink
import dragline.utils.RotationUtils
import dragline.utils.block.BlockUtils
import dragline.utils.extensions.getVec
import dragline.utils.timer.MSTimer
import dragline.value.BlockValue
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue

@ModuleInfo(name = "ChestAura", description = "Automatically opens chests around you.", category = ModuleCategory.WORLD)
object ChestAura : Module() {

    private val rangeValue = FloatValue("Range", 5F, 1F, 6F)
    private val delayValue = IntegerValue("Delay", 100, 50, 200)
    private val throughWallsValue = BoolValue("ThroughWalls", true)
    private val visualSwing = BoolValue("VisualSwing", true)
    private val chestValue = BlockValue("Chest", functions.getIdFromBlock(classProvider.getBlockEnum(BlockType.CHEST)))
    private val rotationsValue = BoolValue("Rotations", true)

    private var currentBlock: WBlockPos? = null
    private val timer = MSTimer()

    val clickedBlocks = mutableListOf<WBlockPos>()

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (DragLine.moduleManager[Blink::class.java]!!.state)
            return

        val thePlayer = mc.thePlayer!!
        val theWorld = mc.theWorld!!

        when (event.eventState) {
            EventState.PRE -> {
                if (classProvider.isGuiContainer(mc.currentScreen))
                    timer.reset()

                val radius = rangeValue.get() + 1

                val eyesPos = WVec3(thePlayer.posX, thePlayer.entityBoundingBox.minY + thePlayer.eyeHeight,
                        thePlayer.posZ)

                currentBlock = BlockUtils.searchBlocks(radius.toInt())
                        .filter {
                            functions.getIdFromBlock(it.value) == chestValue.get() && !clickedBlocks.contains(it.key)
                                    && BlockUtils.getCenterDistance(it.key) < rangeValue.get()
                        }
                        .filter {
                            if (throughWallsValue.get())
                                return@filter true

                            val blockPos = it.key
                            val movingObjectPosition = theWorld.rayTraceBlocks(eyesPos,
                                    blockPos.getVec(), stopOnLiquid = false, ignoreBlockWithoutBoundingBox = true, returnLastUncollidableBlock = false)

                            movingObjectPosition != null && movingObjectPosition.blockPos == blockPos
                        }
                        .minBy { BlockUtils.getCenterDistance(it.key) }?.key

                if (rotationsValue.get())
                    RotationUtils.setTargetRotation((RotationUtils.faceBlock(currentBlock ?: return)
                            ?: return).rotation)
            }

            EventState.POST -> if (currentBlock != null && timer.hasTimePassed(delayValue.get().toLong())) {
                if (mc.playerController.onPlayerRightClick(thePlayer, mc.theWorld!!, thePlayer.heldItem!!, currentBlock!!,
                                classProvider.getEnumFacing(EnumFacingType.DOWN), currentBlock!!.getVec())) {
                    if (visualSwing.get())
                        thePlayer.swingItem()
                    else
                        mc.netHandler.addToSendQueue(classProvider.createCPacketAnimation())

                    clickedBlocks.add(currentBlock!!)
                    currentBlock = null
                    timer.reset()
                }
            }
        }
    }

    override fun onDisable() {
        clickedBlocks.clear()
    }
}