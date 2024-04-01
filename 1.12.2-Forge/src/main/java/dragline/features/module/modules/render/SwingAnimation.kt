/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.render

import dragline.api.MinecraftVersion
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo

@ModuleInfo(name = "SwingAnimation", description = "Changes swing animation.", category = ModuleCategory.RENDER, supportedVersions = [MinecraftVersion.MC_1_8])
class SwingAnimation : Module()