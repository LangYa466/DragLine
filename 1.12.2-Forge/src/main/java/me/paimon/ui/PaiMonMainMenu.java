package me.paimon.ui;

import dragline.DragLine;
import dragline.api.minecraft.client.gui.IGuiScreen;
import dragline.api.minecraft.util.IResourceLocation;
import dragline.ui.client.altmanager.GuiAltManager;
import dragline.ui.cnfont.FontLoaders;
import dragline.ui.font.Fonts;
import dragline.utils.render.AnimationUtils;
import dragline.utils.render.RenderUtils;
import me.paimon.utils.SimpleButton;
import net.ccbluex.liquidbounce.injection.backend.GuiScreenImplKt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;

import static dragline.utils.MinecraftInstance.classProvider;


public class PaiMonMainMenu extends GuiScreen {
    private float currentY;
    private float currentX;
    public float pngAnim = 20f;

    @Override
    public void initGui() {
        this.buttonList.add(new SimpleButton(0, this.width -152, 39+24+12, "Singleplayer"));
        this.buttonList.add(new SimpleButton(1, this.width -152, 39+24+12+25+12, "Multiplayer"));
        this.buttonList.add(new SimpleButton(2, this.width -152, 39+24+12+25+25+12*2, "Alt Manager"));
        this.buttonList.add(new SimpleButton(3, this.width -152, 39+24+12+25+25+25+12*3, "Settings"));
        this.buttonList.add(new SimpleButton(4, this.width -152, 39+24+12+25+25+25+25+12*4, "Shutdown"));

        super.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int h = this.height;
        int w = this.width;

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        drawBackground(0);
        pngAnim = (float) AnimationUtils.animate(250,pngAnim,0.5f);
        float xDiff = ((float) (Mouse.getX() - h / 2) - this.currentX) / (float) scaledResolution.getScaleFactor();
        float yDiff = ((float) (Mouse.getY() - w / 2) - this.currentY) / (float) scaledResolution.getScaleFactor();
        this.currentX += xDiff * 0.3F;
        this.currentY += yDiff * 0.3F;
        RenderUtils.drawRect(0,0,scaledResolution.getScaledWidth(),scaledResolution.getScaledHeight(),new Color(54, 54, 54));

        RenderUtils.drawImage((IResourceLocation) new ResourceLocation("liquidbounce/mainmenu.png"),-pngAnim,0,(scaledResolution.getScaledWidth()),scaledResolution.getScaledHeight());
        Fonts.font40.drawCenteredString("Qing",scaledResolution.getScaledWidth()-120f,39,new Color(244 , 143 , 177).getRGB());
        FontLoaders.CUSTOM18.drawString("Welcome",scaledResolution.getScaledWidth()-250-FontLoaders.CUSTOM64.getStringWidth("Welcome")-20f,scaledResolution.getScaledHeight()-FontLoaders.CUSTOM64.getHeight(),-1);
        FontLoaders.CUSTOM18.drawString("Qing build "+DragLine.CLIENT_VERSION,scaledResolution.getScaledWidth()-250-FontLoaders.CUSTOM18.getStringWidth("Qing build "+ DragLine.CLIENT_VERSION)-26f,scaledResolution.getScaledHeight()-FontLoaders.CUSTOM64.getHeight()-4f-FontLoaders.CUSTOM18.getHeight(),-1);

        Fonts.font40.drawCenteredString("Qing",scaledResolution.getScaledWidth()-120f,37,-1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                this.mc.displayGuiScreen(new GuiWorldSelection(this));
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            }
            case 2: {
                Minecraft.getMinecraft().displayGuiScreen(GuiScreenImplKt.unwrap(classProvider.wrapGuiScreen(new GuiAltManager((IGuiScreen) this))));
                break;
            }
            case 3: {
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            }
            case 4: {
                DragLine.INSTANCE.stopClient();
                mc.shutdown();
            }
        }
    }

}
