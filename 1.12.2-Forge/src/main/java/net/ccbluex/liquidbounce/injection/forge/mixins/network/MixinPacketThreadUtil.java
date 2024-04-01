/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package net.ccbluex.liquidbounce.injection.forge.mixins.network;

import dragline.DragLine;
import dragline.event.PacketProcessEvent;
import net.ccbluex.liquidbounce.injection.backend.PacketImplKt;
import net.minecraft.network.*;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.util.IThreadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PacketThreadUtil.class)
public class MixinPacketThreadUtil {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packetIn, final T processor, IThreadListener scheduler) throws ThreadQuickExitException
    {
        if (!scheduler.isCallingFromMinecraftThread())
        {
            scheduler.addScheduledTask(new Runnable()
            {
                public void run()
                {
                    final PacketProcessEvent event = new PacketProcessEvent(PacketImplKt.wrap(packetIn));
                    DragLine.eventManager.callEvent(event);
                    if (!event.isCancelled()) {
                        try {
                            packetIn.processPacket(processor);
                        } catch (Exception e) {
                            ;
                        }
                    }
                }
            });
            throw ThreadQuickExitException.INSTANCE;
        }
    }
}