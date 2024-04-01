/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.render

import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.FloatValue

@ModuleInfo(name = "NoFOV", description = "Disables FOV changes caused by speed effect, etc.", category = ModuleCategory.RENDER)
class NoFOV : Module() {
    val fovValue = FloatValue("FOV", 1f, 0f, 1.5f)
}
