/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.render

import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.Render3DEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.render.ColorUtils.rainbow
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.IntegerValue
import java.awt.Color
import java.util.*

@ModuleInfo(name = "ProphuntESP", description = "Allows you to see disguised players in PropHunt.", category = ModuleCategory.RENDER)
class ProphuntESP : Module() {
    val blocks: MutableMap<WBlockPos, Long> = HashMap()

    private val colorRedValue = IntegerValue("R", 0, 0, 255)
    private val colorGreenValue = IntegerValue("G", 90, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val colorRainbow = BoolValue("Rainbow", false)

    override fun onDisable() {
        synchronized(blocks) { blocks.clear() }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val color = if (colorRainbow.get()) rainbow() else Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
        for (entity in mc.theWorld!!.loadedEntityList) {
            if (!classProvider.isEntityFallingBlock(classProvider.isEntityFallingBlock(entity)))
                continue

            RenderUtils.drawEntityBox(entity, color, true)
        }
        synchronized(blocks) {
            val iterator: MutableIterator<Map.Entry<WBlockPos, Long>> = blocks.entries.iterator()

            while (iterator.hasNext()) {
                val entry = iterator.next()

                if (System.currentTimeMillis() - entry.value > 2000L) {
                    iterator.remove()
                    continue
                }

                RenderUtils.drawBlockBox(entry.key, color, true)
            }
        }
    }
}