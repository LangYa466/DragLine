/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.movement

import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.block.BlockUtils

@ModuleInfo(name = "AirLadder", description = "Allows you to climb up ladders/vines without touching them.", category = ModuleCategory.MOVEMENT)
class AirLadder : Module() {
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (classProvider.isBlockLadder(BlockUtils.getBlock(WBlockPos(thePlayer.posX, thePlayer.posY + 1, thePlayer.posZ))) && thePlayer.isCollidedHorizontally ||
                classProvider.isBlockVine(BlockUtils.getBlock(WBlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ))) ||
                classProvider.isBlockVine(BlockUtils.getBlock(WBlockPos(thePlayer.posX, thePlayer.posY + 1, thePlayer.posZ)))) {
            thePlayer.motionY = 0.15
            thePlayer.motionX = 0.0
            thePlayer.motionZ = 0.0
        }
    }
}