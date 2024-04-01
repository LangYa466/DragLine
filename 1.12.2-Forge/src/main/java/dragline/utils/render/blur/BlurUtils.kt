/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.utils.render.blur

import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.ResourceLocation
import dragline.utils.ClientUtils
import dragline.utils.MinecraftInstance
import dragline.utils.render.StaticStorage
import  net.minecraft.client.shader.ShaderGroup

object BlurUtils : MinecraftInstance() {
    private val blurShader: ShaderGroup = ShaderGroup(
        mc2.textureManager,
        mc2.resourceManager,
        mc2.framebuffer,
        ResourceLocation("shaders/post/blurArea.json")
    )
    private lateinit var buffer: Framebuffer
    private var lastScale = 0
    private var lastScaleWidth = 0
    private var lastScaleHeight = 0
    private val shaderGroup = ShaderGroup(
        mc2.textureManager,
        mc2.resourceManager,
        mc2.framebuffer,
        ResourceLocation("shaders/post/blurarea.json")
    )
    private val framebuffer = shaderGroup.mainFramebuffer
    private val frbuffer = shaderGroup.getFramebufferRaw("result")

    private var lastFactor = 0
    private var lastWidth = 0
    private var lastHeight = 0

    private var lastX = 0F
    private var lastY = 0F
    private var lastW = 0F
    private var lastH = 0F


    private var lastStrength = 5F

    private fun setupFramebuffers() {
        try {
            shaderGroup.createBindFramebuffers(mc2.displayWidth, mc2.displayHeight)
        } catch (e: Exception) {
            ClientUtils.getLogger().error("Exception caught while setting up shader group", e)
        }
    }

    private fun setValues(
        strength: Float,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        height: Float,
        force: Boolean = false
    ) {
        if (!force && strength == lastStrength && lastX == x && lastY == y && lastW == w && lastH == h) return
        lastStrength = strength
        lastX = x
        lastY = y
        lastW = w
        lastH = h

        for (i in 0..1) {
            shaderGroup.listShaders[i].shaderManager.getShaderUniform("Radius")?.set(strength)
            shaderGroup.listShaders[i].shaderManager.getShaderUniform("BlurXY")?.set(x, height - y - h)
            shaderGroup.listShaders[i].shaderManager.getShaderUniform("BlurCoord")?.set(w, h)
        }
    }


    @JvmStatic
    fun blur(
        posX: Float,
        posY: Float,
        posXEnd: Float,
        posYEnd: Float,
        blurStrength: Float,
        displayClipMask: Boolean,
        triggerMethod: () -> Unit
    ) {
        if (!OpenGlHelper.isFramebufferEnabled()) return

        var x = posX
        var y = posY
        var x2 = posXEnd
        var y2 = posYEnd

        if (x > x2) {
            val z = x
            x = x2
            x2 = z
        }

        if (y > y2) {
            y = y2
            y2 = y
        }

        val sc = ScaledResolution(mc2)
        val scaleFactor = sc.scaleFactor
        val width = sc.scaledWidth
        val height = sc.scaledHeight

        if (sizeHasChanged(scaleFactor, width, height)) {
            setupFramebuffers()
            setValues(blurStrength, x, y, x2 - x, y2 - y, height.toFloat(), true)
        }

        lastFactor = scaleFactor
        lastWidth = width
        lastHeight = height

        setValues(blurStrength, x, y, x2 - x, y2 - y, height.toFloat())

        framebuffer.bindFramebuffer(true)
        shaderGroup.render(mc2.timer.renderPartialTicks)
        mc2.framebuffer.bindFramebuffer(true)

        Stencil.write(displayClipMask)
        triggerMethod()

        Stencil.erase(true)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)
        GlStateManager.pushMatrix()
        GlStateManager.colorMask(true, true, true, false)
        GlStateManager.disableDepth()
        GlStateManager.depthMask(false)
        GlStateManager.enableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableAlpha()
        frbuffer.bindFramebufferTexture()
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
        val f2 = frbuffer.framebufferWidth.toDouble() / frbuffer.framebufferTextureWidth.toDouble()
        val f3 = frbuffer.framebufferHeight.toDouble() / frbuffer.framebufferTextureHeight.toDouble()
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.buffer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR)
        worldrenderer.pos(0.0, height.toDouble(), 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex()
        worldrenderer.pos(width.toDouble(), height.toDouble(), 0.0).tex(f2, 0.0).color(255, 255, 255, 255).endVertex()
        worldrenderer.pos(width.toDouble(), 0.0, 0.0).tex(f2, f3).color(255, 255, 255, 255).endVertex()
        worldrenderer.pos(0.0, 0.0, 0.0).tex(0.0, f3).color(255, 255, 255, 255).endVertex()
        tessellator.draw()
        frbuffer.unbindFramebufferTexture()
        GlStateManager.enableDepth()
        GlStateManager.depthMask(true)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.popMatrix()
        GlStateManager.disableBlend()

        Stencil.dispose()
        GlStateManager.enableAlpha()
    }

    private fun reinitShader() {
        blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight)
        buffer = Framebuffer(mc.displayWidth, mc.displayHeight, true)
        buffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f)
    }

    fun draw(x: Float, y: Float, width: Float, height: Float, radius: Float) {
        val scale = StaticStorage.scaledResolution ?: return
        val factor = scale.scaleFactor
        val factor2 = scale.scaledWidth
        val factor3 = scale.scaledHeight
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3) {
            reinitShader()
        }
        lastScale = factor
        lastScaleWidth = factor2
        lastScaleHeight = factor3
        blurShader.listShaders[0].shaderManager.getShaderUniform("BlurXY")?.set(x, factor3 - y - height)
        blurShader.listShaders[1].shaderManager.getShaderUniform("BlurXY")?.set(x, factor3 - y - height)
        blurShader.listShaders[0].shaderManager.getShaderUniform("BlurCoord")?.set(width, height)
        blurShader.listShaders[1].shaderManager.getShaderUniform("BlurCoord")?.set(width, height)
        blurShader.listShaders[0].shaderManager.getShaderUniform("Radius")?.set(radius)
        blurShader.listShaders[1].shaderManager.getShaderUniform("Radius")?.set(radius)
        blurShader.render(mc.timer.renderPartialTicks)
        mc.framebuffer.bindFramebuffer(true)
    }

    private fun sizeHasChanged(scaleFactor: Int, width: Int, height: Int): Boolean =
        (lastFactor != scaleFactor || lastWidth != width || lastHeight != height)
}
