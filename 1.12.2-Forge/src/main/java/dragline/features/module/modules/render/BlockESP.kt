/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.render

import dragline.api.enums.BlockType
import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.Render3DEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.block.BlockUtils.getBlock
import dragline.utils.block.BlockUtils.getBlockName
import dragline.utils.render.ColorUtils.rainbow
import dragline.utils.render.RenderUtils
import dragline.utils.timer.MSTimer
import dragline.value.BlockValue
import dragline.value.BoolValue
import dragline.value.IntegerValue
import dragline.value.ListValue
import java.awt.Color
import java.util.*

@ModuleInfo(name = "BlockESP", description = "Allows you to see a selected block through walls.", category = ModuleCategory.RENDER)
class BlockESP : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Box", "2D"), "Box")
    private val blockValue = BlockValue("Block", 168)
    private val radiusValue = IntegerValue("Radius", 40, 5, 120)
    private val colorRedValue = IntegerValue("R", 255, 0, 255)
    private val colorGreenValue = IntegerValue("G", 179, 0, 255)
    private val colorBlueValue = IntegerValue("B", 72, 0, 255)
    private val colorRainbow = BoolValue("Rainbow", false)
    private val searchTimer = MSTimer()
    private val posList: MutableList<WBlockPos> = ArrayList()
    private var thread: Thread? = null

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (searchTimer.hasTimePassed(1000L) && (thread == null || !thread!!.isAlive)) {
            val radius = radiusValue.get()
            val selectedBlock = functions.getBlockById(blockValue.get())

            if (selectedBlock == null || selectedBlock == classProvider.getBlockEnum(BlockType.AIR))
                return

            thread = Thread(Runnable {
                val blockList: MutableList<WBlockPos> = ArrayList()

                for (x in -radius until radius) {
                    for (y in radius downTo -radius + 1) {
                        for (z in -radius until radius) {
                            val thePlayer = mc.thePlayer!!

                            val xPos = thePlayer.posX.toInt() + x
                            val yPos = thePlayer.posY.toInt() + y
                            val zPos = thePlayer.posZ.toInt() + z

                            val blockPos = WBlockPos(xPos, yPos, zPos)
                            val block = getBlock(blockPos)

                            if (block == selectedBlock) blockList.add(blockPos)
                        }
                    }
                }
                searchTimer.reset()

                synchronized(posList) {
                    posList.clear()
                    posList.addAll(blockList)
                }
            }, "BlockESP-BlockFinder")

            thread!!.start()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        synchronized(posList) {
            val color = if (colorRainbow.get()) rainbow() else Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
            for (blockPos in posList) {
                when (modeValue.get().toLowerCase()) {
                    "box" -> RenderUtils.drawBlockBox(blockPos, color, true)
                    "2d" -> RenderUtils.draw2D(blockPos, color.rgb, Color.BLACK.rgb)
                }
            }
        }
    }

    override val tag: String
        get() = when {
            blockValue.get() == 26 -> "Bed"
            else -> getBlockName(blockValue.get())
        }
}