/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import dragline.DragLine;
import dragline.event.Render2DEvent;
import dragline.features.module.modules.render.AntiBlind;
import dragline.features.module.modules.client.HUD;
import dragline.features.module.modules.client.NoScoreboard;
import dragline.ui.font.AWTFontRenderer;
import dragline.utils.ClassUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sk1d.fdp.ui.HotbarSettings;

@Mixin(GuiIngame.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiInGame extends MixinGui {

    @Shadow
    @Final
    protected static ResourceLocation WIDGETS_TEX_PATH;
    @Shadow
    @Final
    protected Minecraft mc;

    @Shadow
    protected abstract void renderHotbarItem(int xPos, int yPos, float partialTicks, EntityPlayer player, ItemStack stack);

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(CallbackInfo callbackInfo) {
        if (DragLine.moduleManager.getModule(HUD.class).getState() || NoScoreboard.INSTANCE.getState())
            callbackInfo.cancel();
    }

    /**
     * @author
     */
    @Overwrite
    protected void renderHotbar(ScaledResolution sr, float partialTicks) {

        if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) this.mc.getRenderViewEntity();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            ItemStack itemstack = entityPlayer.getHeldItemOffhand();
            EnumHandSide enumhandside = entityPlayer.getPrimaryHand().opposite();
            int middleScreen = sr.getScaledWidth() / 2;
            float f = this.zLevel;
            this.zLevel = -90.0F;
            final HotbarSettings hotbar = (HotbarSettings) DragLine.moduleManager.getModule(HotbarSettings.class);

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (hotbar.getHotbarValue().get().equals("Minecraft")) {

                GuiIngame.drawRect(middleScreen - 91, sr.getScaledHeight() - 24, middleScreen + 90, sr.getScaledHeight(), Integer.MIN_VALUE);
                GuiIngame.drawRect(middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 1, sr.getScaledHeight() - 24, middleScreen - 91 - 1 + entityPlayer.inventory.currentItem * 20 + 22, sr.getScaledHeight() - 22 - 1 + 24, Integer.MAX_VALUE);
            }

            if (!itemstack.isEmpty())
                GuiIngame.drawRect(middleScreen - 91 - 30, sr.getScaledHeight() - 24, middleScreen - 100, sr.getScaledHeight(), Integer.MIN_VALUE);

            this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);

//            if (!itemstack.isEmpty()) {
//                if (enumhandside == EnumHandSide.LEFT) {
//                    this.drawTexturedModalRect(middleScreen - 91 - 29, sr.getScaledHeight() - 23, 24, 22, 29, 24);
//                } else {
//                    this.drawTexturedModalRect(middleScreen + 91, sr.getScaledHeight() - 23, 53, 22, 29, 24);
//                }
//            }
            this.zLevel = f;
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();

            if (!hotbar.getHotbarValue().get().equals("Minecraft")) {
                for (int l = 0; l < 9; ++l) {
                    int i1 = middleScreen - 90 + l * 20 + 2;
                    int j1 = sr.getScaledHeight() - 16 - 3;
                    this.renderHotbarItem(i1, j1, partialTicks, entityPlayer, entityPlayer.inventory.mainInventory.get(l));
                }
            }

            if (!itemstack.isEmpty()) {
                int l1 = sr.getScaledHeight() - 16 - 3;

                if (enumhandside == EnumHandSide.LEFT) {
                    this.renderHotbarItem(middleScreen - 91 - 26, l1, partialTicks, entityPlayer, itemstack);
                } else {
                    this.renderHotbarItem(middleScreen + 91 + 10, l1, partialTicks, entityPlayer, itemstack);
                }

            }

            if (hotbar.getHotbarValue().get().equals("Minecraft")) {

                for (int l = 0; l < 9; ++l) {
                    int i1 = middleScreen - 90 + l * 20 + 2;
                    int j1 = sr.getScaledHeight() - 16 - 3;
                    this.renderHotbarItem(i1, j1, partialTicks, entityPlayer, entityPlayer.inventory.mainInventory.get(l));
                }
            }

            if (!itemstack.isEmpty()) {
                int l1 = sr.getScaledHeight() - 16 - 3;

                if (enumhandside == EnumHandSide.LEFT) {
                    this.renderHotbarItem(middleScreen - 91 - 26, l1, partialTicks, entityPlayer, itemstack);
                } else {
                    this.renderHotbarItem(middleScreen + 91 + 10, l1, partialTicks, entityPlayer, itemstack);
                }
            }

            if (this.mc.gameSettings.attackIndicator == 2) {
                float f1 = this.mc.player.getCooledAttackStrength(0.0F);

                if (f1 < 1.0F) {
                    int i2 = sr.getScaledHeight() - 20;
                    int j2 = middleScreen + 91 + 6;

                    if (enumhandside == EnumHandSide.RIGHT) {
                        j2 = middleScreen - 91 - 22;
                    }

                    this.mc.getTextureManager().bindTexture(Gui.ICONS);
                    int k1 = (int) (f1 * 19.0F);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.drawTexturedModalRect(j2, i2, 0, 94, 18, 18);
                    this.drawTexturedModalRect(j2, i2 + 18 - k1, 18, 112 - k1, 18, k1);
                }
            }

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected void renderPotionEffects(ScaledResolution p_renderPotionEffects_1_) {
    }

    @Inject(method = "renderHotbar", at = @At("RETURN"))
    private void renderTooltipPost(ScaledResolution sr, float partialTicks, CallbackInfo callbackInfo) {
        if (!ClassUtils.hasClass("net.labymod.api.LabyModAPI")) {
            DragLine.eventManager.callEvent(new Render2DEvent(partialTicks));
            AWTFontRenderer.Companion.garbageCollectionTick();
        }
    }

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPumpkinOverlay(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = (AntiBlind) DragLine.moduleManager.getModule(AntiBlind.class);

        if (antiBlind.getState() && antiBlind.getPumpkinEffect().get())
            callbackInfo.cancel();
    }
}