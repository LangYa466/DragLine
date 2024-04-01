/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.player

import dragline.api.MinecraftVersion
import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo

@ModuleInfo(name = "PotionSaver", description = "Freezes all potion effects while you are standing still.", category = ModuleCategory.PLAYER, supportedVersions = [MinecraftVersion.MC_1_8])
class PotionSaver : Module() {

    @EventTarget
    fun onPacket(e: PacketEvent) {
        val packet = e.packet

        if (classProvider.isCPacketPlayer(packet) && !classProvider.isCPacketPlayerPosition(packet) && !classProvider.isCPacketPlayerPosLook(packet) &&
                !classProvider.isCPacketPlayerPosLook(packet) && mc.thePlayer != null && !mc.thePlayer!!.isUsingItem)
            e.cancelEvent()
    }

}