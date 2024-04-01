/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.world

import dragline.api.enums.BlockType
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo

@ModuleInfo(name = "AutoBreak", description = "Automatically breaks the block you are looking at.", category = ModuleCategory.WORLD)
class AutoBreak : Module() {

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.objectMouseOver == null || mc.objectMouseOver!!.blockPos == null || mc.theWorld == null)
            return

        mc.gameSettings.keyBindAttack.pressed = mc.theWorld!!.getBlockState(mc.objectMouseOver!!.blockPos!!).block != classProvider.getBlockEnum(
            BlockType.AIR)
    }

    override fun onDisable() {
        if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindAttack))
            mc.gameSettings.keyBindAttack.pressed = false
    }
}
