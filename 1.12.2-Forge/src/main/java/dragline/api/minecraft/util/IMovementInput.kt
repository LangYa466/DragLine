/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.util

interface IMovementInput {
    val moveForward: Float
    val moveStrafe: Float
    val jump: Boolean
}