/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package sk1d.fdp.utils;

import dragline.utils.render.AnimationUtils;

public final class Translate {
    private float x;
    private float y;
    private boolean first = false;

    public Translate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void interpolate(float targetX, float targetY, double smoothing) {
        if(first) {
            this.x = (float) AnimationUtils.animate(targetX, this.x, smoothing);
            this.y = (float) AnimationUtils.animate(targetY, this.y, smoothing);
        } else {
            this.x = targetX;
            this.y = targetY;
            first = true;
        }
    }

    public float getY() {
        return this.y;
    }

}
