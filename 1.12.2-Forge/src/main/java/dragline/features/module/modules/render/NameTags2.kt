/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.features.module.modules.render

import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.potion.Potion
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import dragline.api.minecraft.client.entity.IEntityLivingBase
import dragline.api.minecraft.client.entity.player.IEntityPlayer
import dragline.event.EventTarget
import dragline.event.Render3DEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.misc.AntiBot
import dragline.ui.cnfont.FontLoaders
import dragline.utils.EntityUtils
import dragline.utils.render.ColorUtils
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue
import java.awt.Color
import kotlin.math.roundToInt

@ModuleInfo(
    name = "NameTags2",
    description = "Changes the scale of the nametags so you can always read them.",
    category = ModuleCategory.RENDER
)
class NameTags2 : Module() {
    private val healthValue = BoolValue("Health", true)
    private val healthBarValue = BoolValue("Bar", true)
    private val pingValue = BoolValue("Ping", true)
    private val distanceValue = BoolValue("Distance", false)
    private val armorValue = BoolValue("Armor", true)
    private val potionValue = BoolValue("Potions", true)
    private val clearNamesValue = BoolValue("ClearNames", false)
    private val fontValue = FontLoaders.CUSTOM40
    private val fontShadowValue = BoolValue("Shadow", true)
    private val borderValue = BoolValue("Border", true)
    val localValue = BoolValue("LocalPlayer", true)
    val nfpValue = BoolValue("NoFirstPerson", true).displayable { localValue.get() }
    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 0, 0, 255)
    private val borderColorRedValue = IntegerValue("Border-R", 0, 0, 255)
    private val borderColorGreenValue = IntegerValue("Border-G", 0, 0, 255)
    private val borderColorBlueValue = IntegerValue("Border-B", 0, 0, 255)
    private val borderColorAlphaValue = IntegerValue("Border-Alpha", 0, 0, 255)
    private val scaleValue = FloatValue("Scale", 1F, 1F, 4F)

    private val inventoryBackground = ResourceLocation("textures/gui/container/inventory.png")

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
            if (!EntityUtils.isSelected(
                    entity,
                    false
                ) && (!localValue.get() || entity != mc.thePlayer || (nfpValue.get() && mc2.gameSettings.thirdPersonView == 0))
            )
                continue

            renderNameTag(
                entity.asEntityLivingBase(),
                if (clearNamesValue.get())
                    ColorUtils.stripColor(entity.displayName!!.unformattedText) ?: continue
                else
                    entity.displayName!!.unformattedText
            )
        }

        glPopMatrix()
        glPopAttrib()

        // Reset color
        resetColor()
        glColor4f(1F, 1F, 1F, 1F)
    }

    private fun renderNameTag(entity: IEntityLivingBase, tag: String) {
        val fontRenderer = fontValue

        // Modify tag
        val bot = AntiBot.isBot(entity)
        val nameColor = if (bot) "§3" else if (entity.invisible) "§6" else if (entity.sneaking) "§4" else "§7"
        val ping = if (classProvider.isEntityPlayer(entity)) EntityUtils.getPing(entity as IEntityPlayer) else 0

        val distanceText =
            if (distanceValue.get()) "§7 [§a${mc.thePlayer!!.getDistanceToEntity(entity).roundToInt()}m§7]" else ""
        val pingText =
            if (pingValue.get() && classProvider.isEntityPlayer(entity)) (" §7[" + if (ping > 240) "§c" else if (ping > 150) "§e" else "§a") + ping + "ms§7]" else ""
        val healthText = if (healthValue.get()) "§7 [§f" + entity.health.toInt() + "§c❤§7]" else ""
        val botText = if (bot) " §7[§6§lBot§7]" else ""

        val text = "$nameColor$tag$healthText$distanceText$pingText$botText"

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
        var distance = mc.thePlayer!!.getDistanceToEntity(entity) * 0.25f

        if (distance < 1F)
            distance = 1F

        val scale = distance / 100f * scaleValue.get()

        glScalef(-scale, -scale, scale)

        //AWTFontRenderer.assumeNonVolatile = true

        // Draw NameTag
        val width = fontRenderer.getStringWidth(text) * 0.5f

        val dist = width + 4F - (-width - 2F)

        glDisable(GL_TEXTURE_2D)
        glEnable(GL_BLEND)

        val bgColor = Color(
            backgroundColorRedValue.get(),
            backgroundColorGreenValue.get(),
            backgroundColorBlueValue.get(),
            backgroundColorAlphaValue.get()
        )
        val borderColor = Color(
            borderColorRedValue.get(),
            borderColorGreenValue.get(),
            borderColorBlueValue.get(),
            borderColorAlphaValue.get()
        )

        if (borderValue.get())
            RenderUtils.quickDrawBorderedRect(
                -width - 2F,
                -2F,
                width + 4F,
                fontRenderer.height + 2F + if (healthBarValue.get()) 2F else 0F,
                2F,
                borderColor.rgb,
                bgColor.rgb
            )
        else
            RenderUtils.quickDrawRect(
                -width - 2F,
                -2F,
                width + 4F,
                fontRenderer.height + 2F + if (healthBarValue.get()) 2F else 0F,
                bgColor.rgb
            )

        if (healthBarValue.get()) {
            RenderUtils.quickDrawRect(
                -width - 2F,
                fontRenderer.height + 3F,
                -width - 2F + dist,
                fontRenderer.height + 4F,
                Color(10, 155, 10).rgb
            )
            RenderUtils.quickDrawRect(
                -width - 2F,
                fontRenderer.height + 3F,
                -width - 2F + (dist * (entity.health / entity.maxHealth).coerceIn(0F, 1F)),
                fontRenderer.height + 4F,
                Color(10, 255, 10).rgb
            )
        }

        glEnable(GL_TEXTURE_2D)

        fontRenderer.drawString(
            text,
            1F + -width,
            1.5F,
            0xFFFFFF,
            fontShadowValue.get()
        )

        //AWTFontRenderer.assumeNonVolatile = false

        var foundPotion = false
        if (potionValue.get() && classProvider.isEntityPlayer(entity)) {
            val potions =
                entity.activePotionEffects.map { Potion.getPotionById(it.potionID) }.filter { it!!.hasStatusIcon() }
            if (potions.isNotEmpty()) {
                foundPotion = true

                color(1.0F, 1.0F, 1.0F, 1.0F)
                disableLighting()
                enableTexture2D()

                val minX = (potions.size * -20) / 2

                var index = 0

                glPushMatrix()
                enableRescaleNormal()
                for (potion in potions) {
                    color(1.0F, 1.0F, 1.0F, 1.0F)
                    mc2.textureManager.bindTexture(inventoryBackground)
                    val i1 = potion!!.statusIconIndex
                    RenderUtils.drawTexturedModalRect(minX + index * 20, -22, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18, 0F)
                    index++
                }
                disableRescaleNormal()
                glPopMatrix()

                enableAlpha()
                disableBlend()
                enableTexture2D()
            }
        }

        if (armorValue.get() && classProvider.isEntityPlayer(entity)) {
            for (index in 0..4) {
                if (entity.getEquipmentInSlot(index) == null)
                    continue

                mc.renderItem.zLevel = -147F
                mc.renderItem.renderItemAndEffectIntoGUI(
                    entity.getEquipmentInSlot(index)!!,
                    -50 + index * 20,
                    if (potionValue.get() && foundPotion) -42 else -22
                )
            }

            enableAlpha()
            disableBlend()
            enableTexture2D()
        }

        // Pop
        glPopMatrix()
    }
}
