/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import dragline.DragLine;
import dragline.features.module.modules.render.Chams;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sk1d.unknow.module.render.ItemPhysics;

@Mixin(RenderEntityItem.class)
public class MixinRenderEntityItem {

    @Inject(method = "doRender", at = @At("HEAD"))
    private void injectChamsPre(CallbackInfo callbackInfo) {
        final Chams chams = (Chams) DragLine.moduleManager.getModule(Chams.class);

        if (chams.getState() && chams.getItemsValue().get()) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -1000000F);
        }
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void doRender(EntityItem f1, double f2, double f3, double f4, float f5, float j, CallbackInfo ci) {
        ItemPhysics physics = (ItemPhysics) DragLine.moduleManager.getModule(ItemPhysics.class);
        if (physics.getState()) {
            physics.doRender(Minecraft.getMinecraft().getTextureManager(), f1, f2, f3, f4, f5, j);
            ci.cancel();
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void injectChamsPost(CallbackInfo callbackInfo) {
        final Chams chams = (Chams) DragLine.moduleManager.getModule(Chams.class);

        if (chams.getState() && chams.getItemsValue().get()) {
            GL11.glPolygonOffset(1.0F, 1000000F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
    }
}
