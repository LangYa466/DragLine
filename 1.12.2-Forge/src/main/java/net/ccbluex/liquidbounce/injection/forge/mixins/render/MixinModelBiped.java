/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.render;


import dragline.features.module.modules.client.Rotations;
import dragline.utils.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dragline.DragLine.moduleManager;

@Mixin(ModelBiped.class)
@SideOnly(Side.CLIENT)
public class MixinModelBiped {
/*
    @Shadow
    public ModelRenderer bipedRightArm;

    @Shadow
    public ModelRenderer bipedHead;

    @Shadow
    public ModelBiped.ArmPose rightArmPose;

    @Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F"))
    private void revertSwordAnimation(float p_setRotationAngles_1_, float p_setRotationAngles_2_, float p_setRotationAngles_3_, float p_setRotationAngles_4_, float p_setRotationAngles_5_, float p_setRotationAngles_6_, Entity p_setRotationAngles_7_, CallbackInfo callbackInfo) {
        if (rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW)
            this.bipedRightArm.rotateAngleY = 0F;

        if (moduleManager.getModule(Rotations.class).getState() && RotationUtils.serverRotation != null && p_setRotationAngles_7_ instanceof EntityPlayer
                && p_setRotationAngles_7_.equals(Minecraft.getMinecraft().player)) {
            this.bipedHead.rotateAngleX = RotationUtils.serverRotation.getPitch() / (180F / (float) Math.PI);
        }
    }
    */

}