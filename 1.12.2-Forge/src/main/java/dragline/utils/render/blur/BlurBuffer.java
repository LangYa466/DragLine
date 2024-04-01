/*
 * DragLine Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/DragLine/
 */

package dragline.utils.render.blur;


import dragline.DragLine;
import dragline.features.module.modules.client.HUD;
import dragline.utils.render.RenderUtils;

import java.awt.*;

public class BlurBuffer {

    public static void blurArea(float x, float y, float width, float height) {
        final HUD hud = (HUD) DragLine.moduleManager.getModule(HUD.class);
        StencilUtil.initStencilToWrite();
        RenderUtils.drawRect(x, y, x + width, y + height, new Color(-2).getRGB());
        StencilUtil.readStencilBuffer(1);
        GaussianBlur.renderBlur(hud.getRadius().getValue().floatValue());

        StencilUtil.uninitStencilBuffer();
    }

    public static void blurArea2(float x, float y, float x2, float y2) {
        final HUD hud = (HUD) DragLine.moduleManager.getModule(HUD.class);
        StencilUtil.initStencilToWrite();

        RenderUtils.drawRect(x, y, x + (x2 - x), y + (y2 - y), new Color(-2).getRGB());

        StencilUtil.readStencilBuffer(1);
        GaussianBlur.renderBlur(hud.getRadius().getValue().floatValue());
        StencilUtil.uninitStencilBuffer();
    }

    public static void CustomBlurRoundArea(float x, float y, float width, float height, float radius, float BlurStrength) {
        final HUD hud = (HUD) DragLine.moduleManager.getModule(HUD.class);
        StencilUtil.initStencilToWrite();
        RenderUtils.drawRoundedRect2(x, y, x + width, y + height, radius, 6);
        StencilUtil.readStencilBuffer(1);
        GaussianBlur.renderBlur(BlurStrength);
        StencilUtil.uninitStencilBuffer();
    }

    public static void blurAreacustomradius(float x, float y, float width, float height, float radius) {
        final HUD hud = (HUD) DragLine.moduleManager.getModule(HUD.class);
        StencilUtil.initStencilToWrite();
        RenderUtils.drawRect(x, y, x + width, y + height, new Color(-2).getRGB());
        StencilUtil.readStencilBuffer(1);
        GaussianBlur.renderBlur(radius);

        StencilUtil.uninitStencilBuffer();
    }

    public static void blurRoundArea(float x, float y, float width, float height, int radius) {
        StencilUtil.initStencilToWrite();
        RenderUtils.drawRoundedRect2(x, y, x + width, y + height, radius, 6, new Color(-2).getRGB());
        StencilUtil.readStencilBuffer(1);
        GaussianBlur.renderBlur(8.0F);

        StencilUtil.uninitStencilBuffer();
    }
}
