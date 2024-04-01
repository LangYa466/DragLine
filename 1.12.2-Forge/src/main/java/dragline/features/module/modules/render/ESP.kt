/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.features.module.modules.render

import me.nelly.utils.HttpUtils.download
import nellyobfuscator.NellyClassObfuscator
import dragline.DragLine
import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.util.IResourceLocation
import dragline.event.EventTarget
import dragline.event.Render2DEvent
import dragline.event.Render3DEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.ui.font.GameFontRenderer.Companion.getColorIndex
import dragline.utils.ClientUtils
import dragline.utils.EntityUtils
import dragline.utils.render.ColorUtils
import dragline.utils.render.ColorUtils.rainbow
import dragline.utils.render.RenderUtils
import dragline.utils.render.WorldToScreen
import dragline.utils.render.shader.shaders.GlowShader
import dragline.utils.render.shader.shaders.OutlineShader
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue
import dragline.value.ListValue
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector3f
import java.awt.Color
import java.io.File
import java.io.IOException
import java.nio.file.Files

@NellyClassObfuscator
@ModuleInfo(name = "ESP", description = "Allows you to see targets through walls.", category = ModuleCategory.RENDER)
class ESP : Module() {
    @JvmField
    val modeValue = ListValue("Mode", arrayOf("DaTou","Box", "OtherBox", "WireFrame", "2D", "Real2D", "ShaderOutline", "ShaderGlow"), "DaTou")
    private val daTouIMGValue = ListValue("DaTouImg", arrayOf("JiaRan","HuTao","YaoEr","CaoXiaoLong","Paimon","Paimon2","MNWorld-KaKa","MNWorld-NiNi"),"YaoEr").displayable { modeValue.get().toLowerCase().equals("datou") }
    private val shaderOutlineRadius = FloatValue("ShaderOutline-Radius", 1.35f, 1f, 2f)
    private val shaderGlowRadius = FloatValue("ShaderGlow-Radius", 2.3f, 2f, 3f)
    private val colorRedValue = IntegerValue("R", 255, 0, 255)
    private val colorGreenValue = IntegerValue("G", 255, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val colorRainbow = BoolValue("Rainbow", false)
    private val colorTeam = BoolValue("Team", false)

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        val mode = modeValue.get()
        val mvMatrix = WorldToScreen.getMatrix(GL11.GL_MODELVIEW_MATRIX)
        val projectionMatrix = WorldToScreen.getMatrix(GL11.GL_PROJECTION_MATRIX)
        val real2d = mode.equals("real2d", ignoreCase = true)

        //<editor-fold desc="Real2D-Setup">
        if (real2d) {
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glMatrixMode(GL11.GL_PROJECTION)
            GL11.glPushMatrix()
            GL11.glLoadIdentity()
            GL11.glOrtho(0.0, mc.displayWidth.toDouble(), mc.displayHeight.toDouble(), 0.0, -1.0, 1.0)
            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPushMatrix()
            GL11.glLoadIdentity()
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            classProvider.getGlStateManager().enableTexture2D()
            GL11.glDepthMask(true)
            GL11.glLineWidth(1.0f)
        }
        //</editor-fold>
        for (entity in mc.theWorld!!.loadedEntityList) {
            if (entity != mc.thePlayer && EntityUtils.isSelected(entity, false)) {
                val entityLiving = entity.asEntityLivingBase()
                val color = getColor(entityLiving)

                when (mode.toLowerCase()) {
                    "box", "otherbox" -> RenderUtils.drawEntityBox(entity, color, !mode.equals("otherbox", ignoreCase = true))
                    "2d" -> {
                        val renderManager = mc.renderManager
                        val timer = mc.timer
                        val posX: Double = entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX
                        val posY: Double = entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY
                        val posZ: Double = entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
                        RenderUtils.draw2D(entityLiving, posX, posY, posZ, color.rgb, Color.BLACK.rgb)
                    }
                    "datou" -> {

                        var var10000 =
                            entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * mc.timer.renderPartialTicks
                        mc.renderManager
                        val pX: Double = var10000 - mc.renderManager.renderPosX
                        var10000 =
                            entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * mc.timer.renderPartialTicks
                        mc.renderManager
                        val pY: Double = var10000 - mc.renderManager.renderPosY
                        var10000 =
                            entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * mc.timer.renderPartialTicks
                        mc.renderManager
                        val pZ: Double = var10000 - mc.renderManager.renderPosZ
                        GL11.glPushMatrix()
                        GL11.glTranslatef(
                            pX.toFloat(),
                            pY.toFloat() + if (entityLiving.sneaking) 0.8f else 1.3f,
                            pZ.toFloat()
                        )
                        GL11.glNormal3f(1.0f, 1.0f, 1.0f)
                        GL11.glRotatef(-mc.renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
                        GL11.glRotatef(mc.renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
                        val scale = 0.06f
                        GL11.glScalef(-scale, -scale, scale)
                        GL11.glDisable(2896)
                        GL11.glDisable(2929)
                        GL11.glEnable(3042)
                        GL11.glBlendFunc(770, 771)
                        GL11.glPushMatrix()
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)


                        val datousel =
                            when (daTouIMGValue.get().toLowerCase()) {
                                "jiaran" -> "jiaran"
                                "hutao" -> "hutao"
                                "yaoer" -> "yaoer"
                                "caoxiaolong" -> "caoxiaolong"
                                "paimon" -> "paimon"
                                "paimon2" -> "paimon2"
                                "mnworld-kaka" -> "kaka"
                                "mnworld-nini" -> "nini"
                                else -> "wcnmnmsl"
                            }
                        RenderUtils.drawImage(
                            classProvider.createResourceLocation("liquidbounce/datou/$datousel.png"),
                            (-8.0).toInt(),
                            (-14.0).toInt(),
                            16,
                            16
                        )
                        GL11.glPopMatrix()
                        GL11.glPopMatrix()

                    }

                    "real2d" -> {
                        val renderManager = mc.renderManager
                        val timer = mc.timer
                        val bb = entityLiving.entityBoundingBox
                            .offset(-entityLiving.posX, -entityLiving.posY, -entityLiving.posZ)
                            .offset(entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * timer.renderPartialTicks,
                                entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * timer.renderPartialTicks,
                                entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * timer.renderPartialTicks)
                            .offset(-renderManager.renderPosX, -renderManager.renderPosY, -renderManager.renderPosZ)
                        val boxVertices = arrayOf(doubleArrayOf(bb.minX, bb.minY, bb.minZ), doubleArrayOf(bb.minX, bb.maxY, bb.minZ), doubleArrayOf(bb.maxX, bb.maxY, bb.minZ), doubleArrayOf(bb.maxX, bb.minY, bb.minZ), doubleArrayOf(bb.minX, bb.minY, bb.maxZ), doubleArrayOf(bb.minX, bb.maxY, bb.maxZ), doubleArrayOf(bb.maxX, bb.maxY, bb.maxZ), doubleArrayOf(bb.maxX, bb.minY, bb.maxZ))
                        var minX = Float.MAX_VALUE
                        var minY = Float.MAX_VALUE
                        var maxX = -1f
                        var maxY = -1f
                        for (boxVertex in boxVertices) {
                            val screenPos = WorldToScreen.worldToScreen(Vector3f(boxVertex[0].toFloat(), boxVertex[1].toFloat(), boxVertex[2].toFloat()), mvMatrix, projectionMatrix, mc.displayWidth, mc.displayHeight)
                                ?: continue
                            minX = Math.min(screenPos.x, minX)
                            minY = Math.min(screenPos.y, minY)
                            maxX = Math.max(screenPos.x, maxX)
                            maxY = Math.max(screenPos.y, maxY)
                        }
                        if (minX > 0 || minY > 0 || maxX <= mc.displayWidth || maxY <= mc.displayWidth) {
                            GL11.glColor4f(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, 1.0f)
                            GL11.glBegin(GL11.GL_LINE_LOOP)
                            GL11.glVertex2f(minX, minY)
                            GL11.glVertex2f(minX, maxY)
                            GL11.glVertex2f(maxX, maxY)
                            GL11.glVertex2f(maxX, minY)
                            GL11.glEnd()
                        }
                    }
                }
            }
        }
        if (real2d) {
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glMatrixMode(GL11.GL_PROJECTION)
            GL11.glPopMatrix()
            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPopMatrix()
            GL11.glPopAttrib()
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val mode = modeValue.get().toLowerCase()
        val shader = (if (mode.equals("shaderoutline", ignoreCase = true)) OutlineShader.OUTLINE_SHADER else if (mode.equals("shaderglow", ignoreCase = true)) GlowShader.GLOW_SHADER else null)
            ?: return
        shader.startDraw(event.partialTicks)
        renderNameTags = false
        try {
            for (entity in mc.theWorld!!.loadedEntityList) {
                if (!EntityUtils.isSelected(entity, false)) continue
                mc.renderManager.renderEntityStatic(entity, mc.timer.renderPartialTicks, true)
            }
        } catch (ex: Exception) {
            ClientUtils.getLogger().error("An error occurred while rendering all entities for shader esp", ex)
        }
        renderNameTags = true
        val radius = if (mode.equals("shaderoutline", ignoreCase = true)) shaderOutlineRadius.get() else if (mode.equals("shaderglow", ignoreCase = true)) shaderGlowRadius.get() else 1f
        shader.stopDraw(getColor(null), radius, 1f)
    }

    override val tag: String
        get() = (modeValue.get() + if (modeValue.get().toLowerCase().equals("datou")) ", ${daTouIMGValue.get()}" else "")

    fun getColor(entity: IEntity?): Color {
        run {
            if (entity != null && classProvider.isEntityLivingBase(entity)) {
                val entityLivingBase = entity.asEntityLivingBase()

                if (entityLivingBase.hurtTime > 0) return Color.RED
                if (EntityUtils.isFriend(entityLivingBase)) return Color.BLUE
                if (colorTeam.get()) {
                    val chars: CharArray = (entityLivingBase.displayName ?: return@run).formattedText.toCharArray()
                    var color = Int.MAX_VALUE
                    for (i in chars.indices) {
                        if (chars[i] != '§' || i + 1 >= chars.size) continue
                        val index = getColorIndex(chars[i + 1])
                        if (index < 0 || index > 15) continue
                        color = ColorUtils.hexColors[index]
                        break
                    }
                    return Color(color)
                }
            }
        }
        return if (colorRainbow.get()) rainbow() else Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
    }

    companion object {
        @JvmField
        var renderNameTags = true
    }
}