/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.player

import dragline.api.enums.ItemType
import dragline.api.enums.WEnumHand
import dragline.event.EventTarget
import dragline.event.MotionEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo

import dragline.utils.InventoryUtils
import dragline.utils.createUseItemPacket
import dragline.value.ListValue

@ModuleInfo(name = "KeepAlive", description = "Tries to prevent you from dying.", category = ModuleCategory.PLAYER)
class KeepAlive : Module() {

    val modeValue = ListValue("Mode", arrayOf("/heal", "Soup"), "/heal")

    private var runOnce = false

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isDead || thePlayer.health <= 0) {
            if (runOnce) return

            when (modeValue.get().toLowerCase()) {
                "/heal" -> thePlayer.sendChatMessage("/heal")
                "soup" -> {
                    val soupInHotbar = InventoryUtils.findItem(36, 45, classProvider.getItemEnum(ItemType.MUSHROOM_STEW))

                    if (soupInHotbar != -1) {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(soupInHotbar - 36))
                        mc.netHandler.addToSendQueue(createUseItemPacket(thePlayer.inventory.getStackInSlot(soupInHotbar), WEnumHand.MAIN_HAND))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(thePlayer.inventory.currentItem))
                    }
                }
            }

            runOnce = true
        } else
            runOnce = false
    }
}