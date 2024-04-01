package dragline.ui.client.hud.element.elements

import dragline.ui.client.hud.element.Border
import dragline.ui.client.hud.element.Element
import dragline.ui.client.hud.element.ElementInfo
import dragline.ui.font.Fonts
import dragline.utils.MathUtils
import dragline.utils.render.RenderUtils
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.MathHelper
import java.awt.Color
import java.util.stream.Collectors

@ElementInfo("PlayerList")
class PlayerList : Element() {

    override fun drawElement(): Border {
        val players: List<EntityPlayer> =
            mc2.world.playerEntities.stream().filter { p -> p != null && !p.isDead }.collect(Collectors.toList())
        val height: Float = (35 + (players.size - 1) * (Fonts.font16.fontHeight + 8)).toFloat()
        val width = 175f
        val x = 0F
        val y = 0F
        GlStateManager.color(1f, 1f, 1f, 1f)
        RenderUtils.drawRound(x, y, width - 6, height,0f,true, Color(0f, 0f, 0f, 60f))
        RenderUtils.drawRound(x, y, width - 6, 1F,0f,true, Color(97, 194, 162))
        RenderUtils.drawRound(x + 4, y + 17, width - 14, 0.5F, 0f,true,Color(-0x555556))
        Fonts.font22.drawString("Player List", x + 4, y + 5, -1, true)
        Fonts.font18.drawString(
            (players.size + 1).toString(),
            x + width - Fonts.font18.getStringWidth((players.size + 1).toString()) - 10,
            y + 6, -1, true
        )
        for (i in players.indices) {
            val player = players[i]
            renderPlayer(player, i, x, y)
        }
        return Border(0F,0F,width,height)
    }

    private fun renderPlayer(player: EntityPlayer, i: Int, x: Float, y: Float) {
        val height: Float = (Fonts.font16.fontHeight + 8).toFloat()
        val offset = i * height
        val healthPercent: Float = MathHelper.clamp(
            (player.health + player.absorptionAmount) / (player.maxHealth + player.absorptionAmount).toInt(),
            0F,
            1F
        )
        val healthColor = if (healthPercent > .75) Color(66, 246, 123) else if (healthPercent > .5) Color(
            228,
            255,
            105
        ) else if (healthPercent > .35) Color(236, 100, 64) else Color(255, 65, 68)
        val healthText = (MathUtils.round((healthPercent * 100).toDouble(), .01) as Int).toString() + "%"
        Fonts.font16.drawStringWithShadow(
            "§f§l" + player.name + "§r " + healthText,
            (x + 18F).toInt(),
            ((y + offset).toInt()),
            healthColor.rgb
        )
        val headX = x + 4
        val headWH = 32f
        val headY = y + offset + height / 2f - 6
        val f = 0.35f
        RenderUtils.resetColor()
        RenderUtils.scaleStart(headX, headY, f)
        mc2.textureManager.bindTexture((player as AbstractClientPlayer).locationSkin)
        RenderUtils.drawTexturedModalRect(headX.toInt(),
            headY.toInt(), headWH.toInt(), headWH.toInt(), headWH.toInt(), headWH.toInt(),0F)
        RenderUtils.scaleEnd()
        if (player === mc2.player) {
            Fonts.font18.drawStringWithShadow(
                "*",
                (x + 139 - Fonts.font18.getStringWidth("*")).toInt(),
                (y + offset + 6.75f).toInt(),
                 Color(97, 194, 162).rgb
            )
        }
    }

}
