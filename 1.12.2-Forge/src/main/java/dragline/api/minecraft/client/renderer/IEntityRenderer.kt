/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client.renderer

import dragline.api.minecraft.client.shader.IShaderGroup
import dragline.api.minecraft.util.IResourceLocation

interface IEntityRenderer {
    val shaderGroup: IShaderGroup?

    fun disableLightmap()
    fun isShaderActive(): Boolean
    fun loadShader(resourceLocation: IResourceLocation)
    fun stopUseShader()
    fun setupCameraTransform(partialTicks: Float, i: Int)
    fun setupOverlayRendering()
}