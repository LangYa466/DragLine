/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.ui.client.hud.element.elements

import dragline.api.enums.MaterialType
import dragline.ui.client.hud.element.Border
import dragline.ui.client.hud.element.Element
import dragline.ui.client.hud.element.ElementInfo
import dragline.ui.client.hud.element.Side
import dragline.value.BoolValue
import dragline.value.ListValue
import me.nelly.utils.RoundedUtil
import org.lwjgl.opengl.GL11
import java.awt.Color

/**
 * CustomHUD Armor element
 *
 * Shows a horizontal display of current armor
 */
@ElementInfo(name = "Armor")
class Armor(x: Double = -8.0, y: Double = 57.0, scale: Float = 1F,
            side: Side = Side(Side.Horizontal.MIDDLE, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {

    private val modeValue = ListValue("Alignment", arrayOf("Horizontal", "Vertical"), "Horizontal")
    private val rectValue = BoolValue("Rect",true)
    /**
     * Draw element
     */
    override fun drawElement(): Border {
        if (mc.playerController.isNotCreative) {
            GL11.glPushMatrix()

            val renderItem = mc.renderItem
            val isInsideWater = mc.thePlayer!!.isInsideOfMaterial(classProvider.getMaterialEnum(MaterialType.WATER))

            var x = 1
            var y = if (isInsideWater) -10 else 0

            val mode = modeValue.get()

            for (index in 3 downTo 0) {
                val stack = mc.thePlayer!!.inventory.armorInventory[index] ?: continue

                renderItem.renderItemIntoGUI(stack, x, y)
                renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
                if (mode.equals("Horizontal", true))
                    x += 18
                else if (mode.equals("Vertical", true))
                    y += 18
            }

            classProvider.getGlStateManager().enableAlpha()
            classProvider.getGlStateManager().disableBlend()
            classProvider.getGlStateManager().disableLighting()
            classProvider.getGlStateManager().disableCull()
            GL11.glPopMatrix()

            if (modeValue.get().equals("Horizontal", true) && rectValue.get()) {
                RoundedUtil.drawRound(0F, 0F, 72F, 17F,0f,true,Color(0,0,0,60))
            } else if (modeValue.get().equals("Vertical", true) && rectValue.get()){
                RoundedUtil.drawRound(0F, 0F, 18F, 72F,0f,true,Color(0,0,0,60))
            }
        }

        return if (modeValue.get().equals("Horizontal", true))
            Border(0F, 0F, 72F, 17F)
        else
            Border(0F, 0F, 18F, 72F)
    }
}