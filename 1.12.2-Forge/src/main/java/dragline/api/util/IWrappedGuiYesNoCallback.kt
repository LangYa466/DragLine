/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.util

interface IWrappedGuiYesNoCallback {
    fun confirmClicked(result: Boolean, id: Int)
}