/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.potion

interface IPotionEffect {
    fun getDurationString(): String

    val amplifier: Int
    val duration: Int
    val potionID: Int
}