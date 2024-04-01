/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.combat

import dragline.api.enums.EnumFacingType
import dragline.api.minecraft.network.play.client.ICPacketPlayerDigging
import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.RotationUtils
import dragline.value.IntegerValue

@ModuleInfo(name = "FastBow", description = "Turns your bow into a machine gun.", category = ModuleCategory.COMBAT)
class FastBow : Module() {

    private val packetsValue = IntegerValue("Packets", 20, 3, 20)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (!thePlayer.isUsingItem)
            return

        val currentItem = thePlayer.inventory.getCurrentItemInHand()

        if (currentItem != null && classProvider.isItemBow(currentItem.item)) {
            // TODO Find out what this is suppose to do
            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(WBlockPos.ORIGIN, 255, currentItem, 0F, 0F, 0F))

            val yaw = if (RotationUtils.targetRotation != null)
                RotationUtils.targetRotation.yaw
            else
                thePlayer.rotationYaw

            val pitch = if (RotationUtils.targetRotation != null)
                RotationUtils.targetRotation.pitch
            else
                thePlayer.rotationPitch

            for (i in 0 until packetsValue.get())
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerLook(yaw, pitch, true))

            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(
                ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM, WBlockPos.ORIGIN, classProvider.getEnumFacing(
                    EnumFacingType.DOWN)))
            thePlayer.itemInUseCount = currentItem.maxItemUseDuration - 1
        }
    }
}