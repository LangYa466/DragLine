package me.paimon.utils;

import dragline.ui.cnfont.FontLoaders;
import dragline.utils.render.AnimationUtils;
import dragline.utils.render.RenderUtils;
import me.nelly.utils.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public final class SimpleButton
        extends GuiButton {
    private int color = 255;
    private double animation = 0.0;
    private float alpha;

    private float widthFade = 0F;
    public SimpleButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x , y, 70, 25, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY,float p_drawButton_4_) {
        final int delta = RenderUtils.deltaTime;

        final float speedDelta = 0.01F * delta;
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        this.mouseDragged(mc, mouseX, mouseY);
        if (this.hovered) {
            alpha = (float) AnimationUtils.animate(90F, alpha, speedDelta);

            widthFade = (float) AnimationUtils.animate(4F, widthFade, speedDelta);

        } else {
            alpha = (float) AnimationUtils.animate(0f, alpha, speedDelta);

            widthFade  = (float) AnimationUtils.animate(0F, widthFade , speedDelta);
        }
        RoundedUtil.drawRound(this.x-12-widthFade/2,this.y-widthFade/2,90+widthFade,25+widthFade, 4f,new Color(0,0,0, this.alpha / 255F));
        FontLoaders.GenShin.drawCenteredStringWithShadow(this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, new Color(this.color, this.color, this.color).getRGB());
    }
}

