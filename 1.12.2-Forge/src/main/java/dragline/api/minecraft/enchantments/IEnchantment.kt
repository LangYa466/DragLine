/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.enchantments

interface IEnchantment {
    val effectId: Int

    fun getTranslatedName(level: Int): String
}