/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.utils.render.shader.shaders;

import dragline.utils.MinecraftInstance;
import dragline.utils.render.shader.FramebufferShader;
import org.lwjgl.opengl.GL20;

public final class OutlineShader extends FramebufferShader {

    public static final OutlineShader OUTLINE_SHADER = new OutlineShader();

    public OutlineShader() {
        super("outline.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("texture");
        setupUniform("texelSize");
        setupUniform("color");
        setupUniform("divider");
        setupUniform("radius");
        setupUniform("maxSample");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0);
        GL20.glUniform2f(getUniform("texelSize"), 1F / MinecraftInstance.mc.getDisplayWidth() * (radius * quality), 1F / MinecraftInstance.mc.getDisplayHeight() * (radius * quality));
        GL20.glUniform4f(getUniform("color"), red, green, blue, alpha);
        GL20.glUniform1f(getUniform("radius"), radius);
    }
}
