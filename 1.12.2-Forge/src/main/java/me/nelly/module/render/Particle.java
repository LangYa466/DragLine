package me.nelly.module.render;

import dragline.event.EventTarget;
import dragline.event.MotionEvent;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.value.IntegerValue;
import net.minecraft.client.Minecraft;

@ModuleInfo(name = "Particle", description = "Particle", category = ModuleCategory.RENDER)
public class Particle extends Module {
    final IntegerValue redValue = new IntegerValue("Red", 255, 0, 255);
    final IntegerValue greenValue = new IntegerValue("Green", 255, 0, 255);
    final IntegerValue blueValue = new IntegerValue("Blue", 255, 0, 255);

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (mc2.player != null) {
            double posX = Minecraft.getMinecraft().player.posX;
            double posY = Minecraft.getMinecraft().player.posY;
            double posZ = Minecraft.getMinecraft().player.posZ;

            spawnCustomParticles(posX, posY, posZ);
        }
    }

    private void spawnCustomParticles(double x, double y, double z) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.effectRenderer.addEffect(
                new CustomParticle(minecraft.world, x, y, z, 0.0, 0.0, 0.0, redValue.get(), greenValue.get(), blueValue.get()));
    }
}
