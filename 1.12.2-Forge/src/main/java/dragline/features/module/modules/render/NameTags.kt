package dragline.features.module.modules.render

import dragline.api.minecraft.client.entity.IEntityLivingBase
import dragline.event.EventTarget
import dragline.event.Render3DEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.misc.AntiBot
import dragline.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.injection.backend.Backend
import dragline.ui.font.Fonts
import dragline.utils.EntityUtils
import dragline.utils.render.ColorUtils
import dragline.utils.render.RenderUtils
import dragline.utils.render.RenderUtils.drawRect
import dragline.utils.render.RenderUtils.quickDrawRect
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.FontValue
import dragline.value.ListValue
import me.nelly.utils.RoundedUtil
import net.minecraft.client.renderer.GlStateManager.*
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.roundToInt

@ModuleInfo(name = "NameTags", description = "Changes the scale of the nametags so you can always read them.", category = ModuleCategory.RENDER)
class NameTags : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Simple", "Simple2"), "Simple")
    private val armorValue = BoolValue("Armor", true)
    private val clearNamesValue = BoolValue("ClearNames", false)
    private val Healthbar = BoolValue("HealthBar", true)
    private val distanceValue = BoolValue("distance", true)
    private val health = BoolValue("Health", true)
    private val scaleValue = FloatValue("Scale", 1F, 0.7F, 4F)

    private val posYValue = FloatValue("PosY", 0F, 0F, 100F)
    private val fontValue = Fonts.minecraftFont

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        glPushAttrib(GL_ENABLE_BIT)
        glPushMatrix()

        // Disable lightning and depth test
        glDisable(GL_LIGHTING)
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_LINE_SMOOTH)

        // Enable blend
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        for (entity in mc.theWorld!!.loadedEntityList) {
            if (!EntityUtils.isSelected(entity, false))
                continue

            renderNameTag(
                entity.asEntityLivingBase(),
                if (clearNamesValue.get())
                    ColorUtils.stripColor(entity.displayName?.unformattedText) ?: continue
                else
                    (entity.displayName ?: continue).unformattedText
            )
        }

        glPopMatrix()
        glPopAttrib()

        // Reset color
        glColor4f(1F, 1F, 1F, 1F)
    }

    private fun renderNameTag(entity: IEntityLivingBase, tag: String) {
        val thePlayer = mc.thePlayer ?: return
        val fontRenderer = fontValue

        if (modeValue.get().equals("Simple2")) {
            // Modify tag
            val bot = AntiBot.isBot(entity)
            val nameColor = "§7§c"
            val healthText =
                if (distanceValue.get()) " §7${thePlayer.getDistanceToEntity(entity).roundToInt()}m " else ""
            val disText =
                if (entity.health < entity.maxHealth / 4) "§4 " + entity.health.toInt() + " HP" else if (entity.health < entity.maxHealth / 2) "§6 " + entity.health.toInt() + " HP" else "§2 " + entity.health.toInt() + " HP"
            val botText = if (bot) " §c§lBot" else ""

            val text =
                if (health.get()) "$nameColor$tag$healthText$disText$botText" else "$nameColor$tag$healthText$botText"

            // Push
            glPushMatrix()

            // Translate to player position
            val timer = mc.timer
            val renderManager = mc.renderManager


            glTranslated( // Translate to player position with render pos and interpolate it
                entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY + entity.eyeHeight.toDouble() + 0.55,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
            )

            glRotatef(-mc.renderManager.playerViewY, 0F, 1F, 0F)
            glRotatef(mc.renderManager.playerViewX, 1F, 0F, 0F)


            // Scale
            var distance = thePlayer.getDistanceToEntity(entity) * 0.25f

            if (distance < 1F)
                distance = 1F

            val scale = distance / 100f * scaleValue.get()

            glScalef(-scale, -scale, scale)


            // Draw NameTag
            val width = fontRenderer.getStringWidth(text) * 0.5f

            val color =
                if (entity.health <= entity.maxHealth) Color.GREEN else if (entity.health < entity.maxHealth / 2) Color.YELLOW else if (entity.health < entity.maxHealth / 4) Color.RED else Color.RED
            if (Healthbar.get()) {
                drawRect(
                    -width - 2F,
                    fontRenderer.fontHeight + 0F,
                    entity.health / entity.maxHealth * (width + 4F),
                    fontRenderer.fontHeight + 2F,
                    color
                )
            }


            glDisable(GL_TEXTURE_2D)
            glEnable(GL_BLEND)


            quickDrawRect(
                -width - 2F,
                -2F - posYValue.get(),
                width + 4F,
                fontRenderer.fontHeight + 4F,
                Color(0, 0, 0, 70).rgb
            )


            glEnable(GL_TEXTURE_2D)

            fontRenderer.drawString(
                text,
                1F + -width,
                if (fontRenderer == Fonts.minecraftFont) 0F - posYValue.get() else 0.5F - posYValue.get(),
                0xFFFFFF,
                true
            )

            //AWTFontRenderer.assumeNonVolatile = false

            if (armorValue.get() && classProvider.isEntityPlayer(entity)) {
                mc.renderItem.zLevel = -147F

                val indices: IntArray =
                    if (Backend.MINECRAFT_VERSION_MINOR == 8) (0..4).toList().toIntArray() else intArrayOf(
                        0,
                        1,
                        2,
                        3,
                        5,
                        4
                    )

                for (index in indices) {
                    val equipmentInSlot = entity.getEquipmentInSlot(index) ?: continue

                    mc.renderItem.renderItemAndEffectIntoGUI(equipmentInSlot, -50 + index * 20, -22)
                }

                enableAlpha()
                disableBlend()
                enableTexture2D()
            }

            // Pop
            glPopMatrix()
        } else if(modeValue.get().equals("Simple")) {
            val healthPercent = (entity.health / entity.maxHealth).coerceAtMost(1F)
            val width = fontRenderer.getStringWidth(tag).coerceAtLeast(30) / 2
            val maxWidth = width * 2 + 12F
            // Scale
            var distance = thePlayer.getDistanceToEntity(entity) * 0.25f

            if (distance < 1F)
                distance = 1F

            val scale = distance / 100f * scaleValue.get()


            glScalef(-scale * 2f, -scale * 2f, scale * 2f)
            drawRect(-width - 6F, -fontRenderer.fontHeight * 1.7F, width + 6F, -2F, Color(0f, 0f, 0f, 170f).rgb)
            drawRect(
                -width - 6F,
                -2F,
                -width - 6F + (maxWidth * healthPercent),
                0F,
                ColorUtils.healthColor(entity.health, entity.maxHealth, 170)
            )
            drawRect(
                -width - 6F + (maxWidth * healthPercent),
                -2F,
                width + 6F,
                0F,
                Color(0f, 0f, 0f,170f)
            )
            fontRenderer.drawString(
                tag,
                (-fontRenderer.getStringWidth(tag) * 0.5F).toInt(),
                (-fontRenderer.fontHeight * 1.4F).toInt(),
                Color.WHITE.rgb
            )
        }
    }
}
