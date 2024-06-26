/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.util

interface IIChatComponent {
    val unformattedText: String
    val chatStyle: IChatStyle
    val formattedText: String

    fun appendText(text: String)
    fun appendSibling(component: IIChatComponent)
}