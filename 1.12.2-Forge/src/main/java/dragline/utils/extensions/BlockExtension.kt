/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.utils.extensions

import dragline.api.minecraft.util.WBlockPos
import dragline.api.minecraft.util.WVec3
import dragline.utils.block.BlockUtils

/**
 * Get block by position
 */
fun WBlockPos.getBlock() = BlockUtils.getBlock(this)

/**
 * Get vector of block position
 */
fun WBlockPos.getVec() = WVec3(x + 0.5, y + 0.5, z + 0.5)