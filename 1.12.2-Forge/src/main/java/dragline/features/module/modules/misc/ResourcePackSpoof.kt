/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.misc

import dragline.api.minecraft.network.play.client.ICPacketResourcePackStatus
import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.ClientUtils
import java.net.URI
import java.net.URISyntaxException

@ModuleInfo(name = "ResourcePackSpoof", description = "Prevents servers from forcing you to download their resource pack.", category = ModuleCategory.MISC)
class ResourcePackSpoof : Module() {

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (classProvider.isSPacketResourcePackSend(event.packet)) {
            val packet = event.packet.asSPacketResourcePackSend()

            val url = packet.url
            val hash = packet.hash

            try {
                val scheme = URI(url).scheme
                val isLevelProtocol = "level" == scheme

                if ("http" != scheme && "https" != scheme && !isLevelProtocol)
                    throw URISyntaxException(url, "Wrong protocol")

                if (isLevelProtocol && (url.contains("..") || !url.endsWith("/resources.zip")))
                    throw URISyntaxException(url, "Invalid levelstorage resourcepack path")

                mc.netHandler.addToSendQueue(classProvider.createICPacketResourcePackStatus(packet.hash,
                        ICPacketResourcePackStatus.WAction.ACCEPTED))
                mc.netHandler.addToSendQueue(classProvider.createICPacketResourcePackStatus(packet.hash,
                        ICPacketResourcePackStatus.WAction.SUCCESSFULLY_LOADED))
            } catch (e: URISyntaxException) {
                ClientUtils.getLogger().error("Failed to handle resource pack", e)
                mc.netHandler.addToSendQueue(classProvider.createICPacketResourcePackStatus(hash, ICPacketResourcePackStatus.WAction.FAILED_DOWNLOAD))
            }
        }
    }

}