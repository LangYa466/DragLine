/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.special;

import dragline.event.Listenable;
import dragline.utils.MinecraftInstance;

public class AntiForge extends MinecraftInstance implements Listenable {

    public static boolean enabled = false;
    public static boolean blockFML = false;
    public static boolean blockProxyPacket = false;
    public static boolean blockPayloadPackets = false;


    @Override
    public boolean handleEvents() {
        return false;
    }
}