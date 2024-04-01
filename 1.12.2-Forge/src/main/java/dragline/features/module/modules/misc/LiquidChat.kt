/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.misc

import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo

@ModuleInfo(name = "LiquidChat", description = "Allows you to chat with other LiquidBounce users.", category = ModuleCategory.MISC)
class LiquidChat : Module() {
    override fun onEnable() {
        state = false
    }
}