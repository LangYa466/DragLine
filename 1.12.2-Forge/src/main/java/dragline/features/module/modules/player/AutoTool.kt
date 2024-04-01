/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.player

import dragline.api.minecraft.util.WBlockPos
import dragline.event.ClickBlockEvent
import dragline.event.EventTarget
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo

@ModuleInfo(name = "AutoTool", description = "Automatically selects the best tool in your inventory to mine a block.", category = ModuleCategory.PLAYER)
class AutoTool : Module() {

    @EventTarget
    fun onClick(event: ClickBlockEvent) {
        switchSlot(event.clickedBlock ?: return)
    }

    fun switchSlot(blockPos: WBlockPos) {
        var bestSpeed = 1F
        var bestSlot = -1

        val blockState = mc.theWorld!!.getBlockState(blockPos)

        for (i in 0..8) {
            val item = mc.thePlayer!!.inventory.getStackInSlot(i) ?: continue
            val speed = item.getStrVsBlock(blockState)

            if (speed > bestSpeed) {
                bestSpeed = speed
                bestSlot = i
            }
        }

        if (bestSlot != -1)
            mc.thePlayer!!.inventory.currentItem = bestSlot
    }

}