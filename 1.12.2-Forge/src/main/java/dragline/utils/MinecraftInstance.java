/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.utils;

import dragline.DragLine;
import dragline.api.IClassProvider;
import dragline.api.IExtractedFunctions;
import dragline.api.minecraft.client.IMinecraft;
import net.minecraft.client.Minecraft;

public class MinecraftInstance {
    public static final IMinecraft mc = DragLine.wrapper.getMinecraft();
    public static final Minecraft mc2 = Minecraft.getMinecraft();
    public static final IClassProvider classProvider = DragLine.INSTANCE.getWrapper().getClassProvider();
    public static final IExtractedFunctions functions = DragLine.INSTANCE.getWrapper().getFunctions();
}
