/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.world

import dragline.DragLine
import dragline.api.enums.EnumFacingType
import dragline.api.minecraft.network.play.client.ICPacketPlayerDigging
import dragline.api.minecraft.util.WBlockPos
import dragline.api.minecraft.util.WVec3
import dragline.event.EventTarget
import dragline.event.Render3DEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.combat.KillAura
import dragline.features.module.modules.player.AutoTool
import dragline.utils.RotationUtils
import dragline.utils.block.BlockUtils.getBlock
import dragline.utils.block.BlockUtils.getBlockName
import dragline.utils.block.BlockUtils.getCenterDistance
import dragline.utils.block.BlockUtils.isFullBlock
import dragline.value.*
import dragline.utils.render.RenderUtils
import dragline.utils.timer.MSTimer
import dragline.value.*
import java.awt.Color

@ModuleInfo(name = "Fucker", description = "Destroys selected blocks around you. (aka.  IDNuker)", category = ModuleCategory.WORLD)
object Fucker : Module() {

    /**
     * SETTINGS
     */

    private val blockValue = BlockValue("Block", 26)
    private val throughWallsValue = ListValue("ThroughWalls", arrayOf("None", "Raycast", "Around"), "None")
    private val rangeValue = FloatValue("Range", 5F, 1F, 7F)
    private val actionValue = ListValue("Action", arrayOf("Destroy", "Use"), "Destroy")
    private val instantValue = BoolValue("Instant", false)
    private val switchValue = IntegerValue("SwitchDelay", 250, 0, 1000)
    private val swingValue = BoolValue("Swing", true)
    private val rotationsValue = BoolValue("Rotations", true)
    private val surroundingsValue = BoolValue("Surroundings", true)
    private val noHitValue = BoolValue("NoHit", false)


    /**
     * VALUES
     */

    private var pos: WBlockPos? = null
    private var oldPos: WBlockPos? = null
    private var blockHitDelay = 0
    private val switchTimer = MSTimer()
    var currentDamage = 0F

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (noHitValue.get()) {
            val killAura = DragLine.moduleManager.getModule(KillAura::class.java) as KillAura

            if (killAura.state && killAura.target != null)
                return
        }

        val targetId = blockValue.get()

        if (pos == null || functions.getIdFromBlock(getBlock(pos!!)!!) != targetId ||
                getCenterDistance(pos!!) > rangeValue.get())
            pos = find(targetId)

        // Reset current breaking when there is no target block
        if (pos == null) {
            currentDamage = 0F
            return
        }

        var currentPos = pos ?: return
        var rotations = RotationUtils.faceBlock(currentPos) ?: return

        // Surroundings
        var surroundings = false

        if (surroundingsValue.get()) {
            val eyes = thePlayer.getPositionEyes(1F)
            val blockPos = mc.theWorld!!.rayTraceBlocks(eyes, rotations.vec, false,
                    false, true)?.blockPos

            if (blockPos != null && !classProvider.isBlockAir(blockPos)) {
                if (currentPos.x != blockPos.x || currentPos.y != blockPos.y || currentPos.z != blockPos.z)
                    surroundings = true

                pos = blockPos
                currentPos = pos ?: return
                rotations = RotationUtils.faceBlock(currentPos) ?: return
            }
        }

        // Reset switch timer when position changed
        if (oldPos != null && oldPos != currentPos) {
            currentDamage = 0F
            switchTimer.reset()
        }

        oldPos = currentPos

        if (!switchTimer.hasTimePassed(switchValue.get().toLong()))
            return

        // Block hit delay
        if (blockHitDelay > 0) {
            blockHitDelay--
            return
        }

        // Face block
        if (rotationsValue.get())
            RotationUtils.setTargetRotation(rotations.rotation)

        when {
            // Destory block
            actionValue.get().equals("destroy", true) || surroundings -> {
                // Auto Tool
                val autoTool = DragLine.moduleManager[AutoTool::class.java] as AutoTool
                if (autoTool.state)
                    autoTool.switchSlot(currentPos)

                // Break block
                if (instantValue.get()) {
                    // CivBreak style block breaking
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(
                        ICPacketPlayerDigging.WAction.START_DESTROY_BLOCK,
                            currentPos, classProvider.getEnumFacing(EnumFacingType.DOWN)))

                    if (swingValue.get())
                        thePlayer.swingItem()

                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(
                        ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK,
                            currentPos, classProvider.getEnumFacing(EnumFacingType.DOWN)))
                    currentDamage = 0F
                    return
                }

                // Minecraft block breaking
                val block = currentPos.getBlock() ?: return

                if (currentDamage == 0F) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(
                        ICPacketPlayerDigging.WAction.START_DESTROY_BLOCK,
                            currentPos, classProvider.getEnumFacing(EnumFacingType.DOWN)))

                    if (thePlayer.capabilities.isCreativeMode ||
                            block.getPlayerRelativeBlockHardness(thePlayer, mc.theWorld!!, pos!!) >= 1.0F) {
                        if (swingValue.get())
                            thePlayer.swingItem()
                        mc.playerController.onPlayerDestroyBlock(pos!!, classProvider.getEnumFacing(EnumFacingType.DOWN))

                        currentDamage = 0F
                        pos = null
                        return
                    }
                }

                if (swingValue.get())
                    thePlayer.swingItem()

                currentDamage += block.getPlayerRelativeBlockHardness(thePlayer, mc.theWorld!!, currentPos)
                mc.theWorld!!.sendBlockBreakProgress(thePlayer.entityId, currentPos, (currentDamage * 10F).toInt() - 1)

                if (currentDamage >= 1F) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(
                        ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK,
                            currentPos, classProvider.getEnumFacing(EnumFacingType.DOWN)))
                    mc.playerController.onPlayerDestroyBlock(currentPos, classProvider.getEnumFacing(EnumFacingType.DOWN))
                    blockHitDelay = 4
                    currentDamage = 0F
                    pos = null
                }
            }

            // Use block
            actionValue.get().equals("use", true) -> if (mc.playerController.onPlayerRightClick(
                            thePlayer, mc.theWorld!!, thePlayer.heldItem!!, pos!!, classProvider.getEnumFacing(
                        EnumFacingType.DOWN),
                            WVec3(currentPos.x.toDouble(), currentPos.y.toDouble(), currentPos.z.toDouble())
                )) {
                if (swingValue.get())
                    thePlayer.swingItem()

                blockHitDelay = 4
                currentDamage = 0F
                pos = null
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawBlockBox(pos ?: return, Color.RED, true)
    }

    /**
     * Find new target block by [targetID]
     */
    /*private fun find(targetID: Int) =
        searchBlocks(rangeValue.get().toInt() + 1).filter {
                    Block.getIdFromBlock(it.value) == targetID && getCenterDistance(it.key) <= rangeValue.get()
                            && (isHitable(it.key) || surroundingsValue.get())
                }.minBy { getCenterDistance(it.key) }?.key*/

    //Removed triple iteration of blocks to improve speed
    /**
     * Find new target block by [targetID]
     */
    private fun find(targetID: Int): WBlockPos? {
        val thePlayer = mc.thePlayer ?: return null

        val radius = rangeValue.get().toInt() + 1

        var nearestBlockDistance = Double.MAX_VALUE
        var nearestBlock: WBlockPos? = null

        for (x in radius downTo -radius + 1) {
            for (y in radius downTo -radius + 1) {
                for (z in radius downTo -radius + 1) {
                    val blockPos = WBlockPos(thePlayer.posX.toInt() + x, thePlayer.posY.toInt() + y,
                            thePlayer.posZ.toInt() + z)
                    val block = getBlock(blockPos) ?: continue

                    if (functions.getIdFromBlock(block) != targetID) continue

                    val distance = getCenterDistance(blockPos)
                    if (distance > rangeValue.get()) continue
                    if (nearestBlockDistance < distance) continue
                    if (!isHitable(blockPos) && !surroundingsValue.get()) continue

                    nearestBlockDistance = distance
                    nearestBlock = blockPos
                }
            }
        }

        return nearestBlock
    }

    /**
     * Check if block is hitable (or allowed to hit through walls)
     */
    private fun isHitable(blockPos: WBlockPos): Boolean {
        val thePlayer = mc.thePlayer ?: return false

        return when (throughWallsValue.get().toLowerCase()) {
            "raycast" -> {
                val eyesPos = WVec3(thePlayer.posX, thePlayer.entityBoundingBox.minY +
                        thePlayer.eyeHeight, thePlayer.posZ)
                val movingObjectPosition = mc.theWorld!!.rayTraceBlocks(eyesPos,
                        WVec3(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5), stopOnLiquid = false,
                        ignoreBlockWithoutBoundingBox = true, returnLastUncollidableBlock = false)

                movingObjectPosition != null && movingObjectPosition.blockPos == blockPos
            }
            "around" -> !isFullBlock(blockPos.down()) || !isFullBlock(blockPos.up()) || !isFullBlock(blockPos.north())
                    || !isFullBlock(blockPos.east()) || !isFullBlock(blockPos.south()) || !isFullBlock(blockPos.west())
            else -> true
        }
    }

    override val tag: String
        get() = when {
            blockValue.get() == 26 -> "Bed"
            else -> getBlockName(blockValue.get())
        }
}