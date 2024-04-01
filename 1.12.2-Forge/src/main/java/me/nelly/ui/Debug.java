package me.nelly.ui;

import dragline.api.minecraft.client.gui.IFontRenderer;
import dragline.ui.client.hud.element.Border;
import dragline.ui.client.hud.element.Element;
import dragline.ui.client.hud.element.ElementInfo;
import dragline.ui.font.Fonts;
import dragline.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityPlayerSP;

import java.awt.*;

@ElementInfo(name = "Info")
public class Debug extends Element {


    private float toFloat(final double value) {
        return (int)value + (int)(value * 10.0) % 10 / 10.0f;
    }

    private float toDouble(final double value) {
        return (int)value + (int)(value * 100.0) % 100 / 100.0f;
    }


    public Border drawElement() {
        final EntityPlayerSP player = mc2.player;
        final IFontRenderer A16 = Fonts.font16;
        final int fonth = A16.getFontHeight();
        RenderUtils.drawRect(0,0, 120, 200, new Color(20, 20, 20, 180).getRGB());
        A16.drawStringWithShadow("Health: " + this.toFloat(player.getHealth()), 0, fonth, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("X:" + this.toFloat(player.posX) + " Y:" + this.toFloat(player.posY) + " Z:" + this.toFloat(player.posZ), 0, (int) (getY() + fonth - fonth), new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Motion X:" + this.toDouble(player.motionX) + " Y:" + this.toDouble(player.motionY) + " Z:" + this.toDouble(player.motionZ), 0,  (int) (getY() + fonth - fonth - fonth), new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Hurt Time: " + player.hurtTime, 0, 10, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Hurt ResistantTime Time: " + player.hurtResistantTime, 0, 0, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Yaw: " + this.toFloat(player.rotationYaw) + " Pitch" + this.toFloat(player.rotationPitch), 0, 0, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Head: " + this.toFloat(player.rotationYawHead) + " Body: " + this.toFloat(player.renderYawOffset), 0, 0 , new Color(255, 255, 255).getRGB());
        return new Border(20, 60, 150, 200);
    }
}
