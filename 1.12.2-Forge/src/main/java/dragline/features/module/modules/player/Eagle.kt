/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.player

import dragline.api.enums.BlockType
import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo

@ModuleInfo(name = "Eagle", description = "Makes you eagle (aka. FastBridge).", category = ModuleCategory.PLAYER)
class Eagle : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        mc.gameSettings.keyBindSneak.pressed = mc.theWorld!!.getBlockState(WBlockPos(thePlayer.posX, thePlayer.posY - 1.0, thePlayer.posZ)).block == classProvider.getBlockEnum(
            BlockType.AIR)
    }

    override fun onDisable() {
        if (mc.thePlayer == null)
            return

        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false
    }
}
