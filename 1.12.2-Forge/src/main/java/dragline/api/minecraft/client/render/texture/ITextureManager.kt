/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client.render.texture

import dragline.api.minecraft.util.IResourceLocation

interface ITextureManager {
    fun loadTexture(textureLocation: IResourceLocation, textureObj: IAbstractTexture): Boolean
    fun bindTexture(image: IResourceLocation)
}