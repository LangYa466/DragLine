/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.world.border

import dragline.api.minecraft.util.WBlockPos

interface IWorldBorder {
    fun contains(blockPos: WBlockPos): Boolean
}