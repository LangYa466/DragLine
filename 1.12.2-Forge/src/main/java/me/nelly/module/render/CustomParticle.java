package me.nelly.module.render;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CustomParticle extends Particle {
    private float initialRed;
    private float initialGreen;
    private float initialBlue;

    public CustomParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float red, float green, float blue) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.initialRed = red;
        this.initialGreen = green;
        this.initialBlue = blue;
        this.setParticleTextureIndex(0);
        this.particleMaxAge = 20;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;

        float ageMultiplier = (float)this.particleAge / (float)this.particleMaxAge;
        this.particleRed = MathHelper.clamp(initialRed * (1 - ageMultiplier), 0.0f, 1.0f);
        this.particleGreen = MathHelper.clamp(initialGreen * (1 - ageMultiplier), 0.0f, 1.0f);
        this.particleBlue = MathHelper.clamp(initialBlue * (1 - ageMultiplier), 0.0f, 1.0f);
    }
}
