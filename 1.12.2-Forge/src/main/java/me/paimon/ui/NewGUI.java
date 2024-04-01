/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package me.paimon.ui;

import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.utils.render.ColorUtils;
import dragline.utils.render.RenderUtils;
import dragline.value.BoolValue;
import dragline.value.FloatValue;
import dragline.value.IntegerValue;
import dragline.value.ListValue;
import me.paimon.newVer.NewUi;

import java.awt.*;

@ModuleInfo(name = "NewGUI", description = "next generation clickgui.", category = ModuleCategory.RENDER, canEnable = false)
public class NewGUI extends Module {
    public static final BoolValue fastRenderValue = new BoolValue("FastRender", false);

    private static final ListValue colorModeValue = new ListValue("Color", new String[] {"Custom", "Sky", "Rainbow", "LiquidSlowly", "Fade"}, "Custom");
    private static final IntegerValue colorRedValue = new IntegerValue("Red", 0, 0, 255);
    private static final IntegerValue colorGreenValue = new IntegerValue("Green", 140, 0, 255);
    private static final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
    private static final FloatValue saturationValue = new FloatValue("Saturation", 1F, 0F, 1F);
    private static final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
    private static final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);
    public double slide, progress = 0;
    @Override
    public void onEnable() {
        slide = progress = 0;
        mc2.displayGuiScreen(NewUi.getInstance());
    }


    public static Color getAccentColor() {
        Color c = new Color(255, 255, 255, 255);
        switch (colorModeValue.get().toLowerCase()) {
            case "custom":
                c = new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
                break;
            case "rainbow":
                c = new Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0));
                break;
            case "sky":
                c = RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
                break;
            case "liquidslowly":
                c = ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get());
                break;
            case "fade":
                c = ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
                break;
        }
        return c;
    }
}
