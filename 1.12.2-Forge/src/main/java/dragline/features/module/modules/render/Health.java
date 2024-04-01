/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.render;


import dragline.api.minecraft.util.IScaledResolution;
import dragline.event.EventTarget;
import dragline.event.Render2DEvent;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.ui.font.Fonts;
import dragline.utils.render.ColorUtils;
import dragline.value.BoolValue;
import dragline.value.IntegerValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "Health", description = "sb.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_RSHIFT)
public class Health extends Module {

    private final IntegerValue colorRedValue = new IntegerValue("R", 255, 0, 255);
    private final IntegerValue  colorGreenValue = new IntegerValue("G", 255, 0, 255);
    private final IntegerValue  colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private final BoolValue cColorValue = new BoolValue("CustomColor", false);
    private final BoolValue cFontValue = new BoolValue("CustomFont", false);


    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        IScaledResolution sr = classProvider.createScaledResolution(mc);
        float healthNum = (float)Math.round((mc.getThePlayer().getHealth() * 10.0) / 10.0F);
        float abNum = (float)Math.round((Minecraft.getMinecraft().player.getAbsorptionAmount() * 10.0) / 10.0F);

        //自定义字体时
        String abString = this.cFontValue.get() ? "§e" + abNum + "§r" :
                //我的世界原版字体时
                "§e" + abNum + "§e❤";

        if (Minecraft.getMinecraft().player.getAbsorptionAmount() <= 0.0F)
            abString = "";

        //自定义字体
        String text =  this.cFontValue.get() ? healthNum + "§r " + abString :
                //我的世界原版字体
                healthNum + "§c❤ " + abString;

        int c = this.cColorValue.get() ? new Color(this.colorRedValue.get(), this.colorGreenValue.get(), this.colorBlueValue.get()).getRGB() : ColorUtils.INSTANCE.getHealthColor2(mc.getThePlayer().getHealth(), mc.getThePlayer().getMaxHealth());
        //自定义字体
        if (this.cFontValue.get()) {
            Fonts.font35.drawCenteredString(text, (float) sr.getScaledWidth() / 2, (float) sr.getScaledHeight() / 2 - 25, c, true);
            //我的世界原版字体
        } else {
            mc.getFontRendererObj().drawCenteredString(text, (float) sr.getScaledWidth() / 2, (float) sr.getScaledHeight() / 2 - 25, c, true);
        }
    }
}
