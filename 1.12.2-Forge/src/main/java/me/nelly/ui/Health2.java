//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.nelly.ui;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

import dragline.api.minecraft.potion.PotionType;
import dragline.event.EventTarget;
import dragline.event.Render2DEvent;
import dragline.event.UpdateEvent;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.utils.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

@ModuleInfo(name = "Health2",category = ModuleCategory.RENDER,description = "Health2")
public final class Health2 extends Module {
    private final DecimalFormat decimalFormat = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ENGLISH));
    private final Random random = new Random();
    private int width;

    @EventTarget
    public void onRenderGuiEvent(UpdateEvent event) {
        if (mc2.currentScreen instanceof GuiInventory || mc2.currentScreen instanceof GuiChest || mc2.currentScreen instanceof GuiContainerCreative) {
            this.renderHealth();
        }

    }

    @EventTarget
    public void onRender2DEvent(Render2DEvent event) {
        if (!(mc2.currentScreen instanceof GuiInventory) && !(mc2.currentScreen instanceof GuiChest)) {
            this.renderHealth();
        }

    }

    private void renderHealth() {
        ScaledResolution scaledResolution = new ScaledResolution(mc2);
        GuiScreen screen = mc2.currentScreen;
        float absorptionHealth = mc2.player.getAbsorptionAmount();
        String string = this.decimalFormat.format(mc2.player.getHealth() / 2.0F) + "§c❤ " + (absorptionHealth <= 0.0F ? "" : "§e" + this.decimalFormat.format(absorptionHealth / 2.0F) + "§6❤");
        int offsetY = 0;
        if (mc2.player.getHealth() >= 0.0F && mc2.player.getHealth() < 10.0F || mc2.player.getHealth() >= 10.0F && mc2.player.getHealth() < 100.0F) {
            this.width = 3;
        }

        if (screen instanceof GuiInventory) {
            offsetY = 70;
        } else if (screen instanceof GuiContainerCreative) {
            offsetY = 80;
        } else if (screen instanceof GuiChest) {
            offsetY = ((GuiChest) screen).getYSize() / 2 - 15;
        }

        int x = (new ScaledResolution(mc2)).getScaledWidth() / 2 - this.width;
        int y = (new ScaledResolution(mc2)).getScaledHeight() / 2 + 25 + offsetY;
        Color color = Colors.blendColors(new float[]{0.0F, 0.5F, 1.0F}, new Color[]{new Color(255, 37, 0), Color.YELLOW, Color.GREEN}, mc2.player.getHealth() / mc2.player.getMaxHealth());
        Minecraft.getMinecraft().fontRenderer.drawString(string, absorptionHealth > 0.0F ? (float) x - 15.5F : (float) x - 3.5F, (float) y, color.getRGB(), true);
        GL11.glPushMatrix();
        mc2.getTextureManager().bindTexture(Gui.ICONS);
        this.random.setSeed((long) mc2.ingameGUI.getUpdateCounter() * 312871L);
        float width = (float) scaledResolution.getScaledWidth() / 2.0F - mc2.player.getMaxHealth() / 2.5F * 10.0F / 2.0F;
        float maxHealth = mc2.player.getMaxHealth();
        int lastPlayerHealth = mc2.ingameGUI.lastPlayerHealth;
        int healthInt = MathHelper.ceil(mc2.player.getHealth());
        int l2 = -1;
        boolean flag = mc2.ingameGUI.healthUpdateCounter > (long) mc2.ingameGUI.getUpdateCounter() && (mc2.ingameGUI.healthUpdateCounter - (long) mc2.ingameGUI.getUpdateCounter()) / 3L % 2L == 1L;
        if (mc.getThePlayer().isPotionActive(classProvider.getPotionEnum(PotionType.REGENERATION))) {
            l2 = mc2.ingameGUI.getUpdateCounter() % MathHelper.ceil(maxHealth + 5.0F);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i6 = MathHelper.ceil(maxHealth / 2.0F) - 1; i6 >= 0; --i6) {
            int xOffset = 16;
            if (mc.getThePlayer().isPotionActive(classProvider.getPotionEnum(PotionType.POISON))) {
                xOffset += 36;
                if (mc.getThePlayer().isPotionActive(classProvider.getPotionEnum(PotionType.WITHER))) {
                    xOffset += 72;
                }

                int k3 = 0;
                if (flag) {
                    k3 = 1;
                }

                float renX = width + (float) (i6 % 10 * 8);
                float renY = (float) scaledResolution.getScaledHeight() / 2.0F + 15.0F + (float) offsetY;
                if (healthInt <= 4) {
                    renY += (float) this.random.nextInt(2);
                }

                if (i6 == l2) {
                    renY -= 2.0F;
                }

                int yOffset = 0;
                if (mc2.world.getWorldInfo().isHardcoreModeEnabled()) {
                    yOffset = 5;
                }


                drawTexturedModalRect(renX, renY, 16 + k3 * 9, 9 * yOffset);
                if (flag) {
                    if (i6 * 2 + 1 < lastPlayerHealth) {
                        drawTexturedModalRect(renX, renY, xOffset + 54, 9 * yOffset);
                    }

                    if (i6 * 2 + 1 == lastPlayerHealth) {
                        drawTexturedModalRect(renX, renY, xOffset + 63, 9 * yOffset);
                    }
                }

                if (i6 * 2 + 1 < healthInt) {
                    drawTexturedModalRect(renX, renY, xOffset + 36, 9 * yOffset);
                }

                if (i6 * 2 + 1 == healthInt) {
                    drawTexturedModalRect(renX, renY, xOffset + 45, 9 * yOffset);
                }
            }

            GL11.glPopMatrix();
        }


    }
    private static float zLevel;

    public static void drawTexturedModalRect(float renX, float renY, int p_drawTexturedModalRect_3_, int p_drawTexturedModalRect_4_) {
        Tessellator lvt_9_1_ = Tessellator.getInstance();
        BufferBuilder lvt_10_1_ = lvt_9_1_.getBuffer();
        lvt_10_1_.begin(7, DefaultVertexFormats.POSITION_TEX);
        lvt_10_1_.pos(renX + 0.0F, renY + (float) 9, zLevel).tex((float) (p_drawTexturedModalRect_3_) * 0.00390625F, (float) (p_drawTexturedModalRect_4_ + 9) * 0.00390625F).endVertex();
        lvt_10_1_.pos(renX + (float) 9, renY + (float) 9, zLevel).tex((float) (p_drawTexturedModalRect_3_ + 9) * 0.00390625F, (float) (p_drawTexturedModalRect_4_ + 9) * 0.00390625F).endVertex();
        lvt_10_1_.pos(renX + (float) 9, renY + 0.0F, zLevel).tex((float) (p_drawTexturedModalRect_3_ + 9) * 0.00390625F, (float) (p_drawTexturedModalRect_4_) * 0.00390625F).endVertex();
        lvt_10_1_.pos(renX + 0.0F, renY + 0.0F, zLevel).tex((float) (p_drawTexturedModalRect_3_) * 0.00390625F, (float) (p_drawTexturedModalRect_4_) * 0.00390625F).endVertex();
        lvt_9_1_.draw();
    }
}
