/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.combat

import dragline.api.enums.EnumFacingType
import dragline.api.enums.ItemType
import dragline.api.enums.WEnumHand
import dragline.api.minecraft.network.play.client.ICPacketPlayerDigging
import dragline.api.minecraft.util.WBlockPos
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.InventoryUtils
import dragline.utils.createOpenInventoryPacket
import dragline.utils.createUseItemPacket
import dragline.utils.timer.MSTimer
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue
import dragline.value.ListValue

@ModuleInfo(name = "AutoSoup", description = "Makes you automatically eat soup whenever your health is low.", category = ModuleCategory.COMBAT)
class AutoSoup : Module() {

    private val healthValue = FloatValue("Health", 15f, 0f, 20f)
    private val delayValue = IntegerValue("Delay", 150, 0, 500)
    private val openInventoryValue = BoolValue("OpenInv", false)
    private val simulateInventoryValue = BoolValue("SimulateInventory", true)
    private val bowlValue = ListValue("Bowl", arrayOf("Drop", "Move", "Stay"), "Drop")

    private val timer = MSTimer()

    override val tag: String
        get() = healthValue.get().toString()

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (!timer.hasTimePassed(delayValue.get().toLong()))
            return

        val thePlayer = mc.thePlayer ?: return

        val soupInHotbar = InventoryUtils.findItem(36, 45, classProvider.getItemEnum(ItemType.MUSHROOM_STEW))

        if (thePlayer.health <= healthValue.get() && soupInHotbar != -1) {
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(soupInHotbar - 36))
            mc.netHandler.addToSendQueue(createUseItemPacket(thePlayer.inventory.getStackInSlot(soupInHotbar), WEnumHand.MAIN_HAND))

            if (bowlValue.get().equals("Drop", true))
                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(
                    ICPacketPlayerDigging.WAction.DROP_ITEM, WBlockPos.ORIGIN, classProvider.getEnumFacing(
                        EnumFacingType.DOWN)))

            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(thePlayer.inventory.currentItem))
            timer.reset()
            return
        }

        val bowlInHotbar = InventoryUtils.findItem(36, 45, classProvider.getItemEnum(ItemType.BOWL))
        if (bowlValue.get().equals("Move", true) && bowlInHotbar != -1) {
            if (openInventoryValue.get() && !classProvider.isGuiInventory(mc.currentScreen))
                return

            var bowlMovable = false

            for (i in 9..36) {
                val itemStack = thePlayer.inventory.getStackInSlot(i)

                if (itemStack == null) {
                    bowlMovable = true
                    break
                } else if (itemStack.item == classProvider.getItemEnum(ItemType.BOWL) && itemStack.stackSize < 64) {
                    bowlMovable = true
                    break
                }
            }

            if (bowlMovable) {
                val openInventory = !classProvider.isGuiInventory(mc.currentScreen) && simulateInventoryValue.get()

                if (openInventory)
                    mc.netHandler.addToSendQueue(createOpenInventoryPacket())

                mc.playerController.windowClick(0, bowlInHotbar, 0, 1, thePlayer)
            }
        }

        val soupInInventory = InventoryUtils.findItem(9, 36, classProvider.getItemEnum(ItemType.MUSHROOM_STEW))

        if (soupInInventory != -1 && InventoryUtils.hasSpaceHotbar()) {
            if (openInventoryValue.get() && !classProvider.isGuiInventory(mc.currentScreen))
                return

            val openInventory = !classProvider.isGuiInventory(mc.currentScreen) && simulateInventoryValue.get()
            if (openInventory)
                mc.netHandler.addToSendQueue(createOpenInventoryPacket())

            mc.playerController.windowClick(0, soupInInventory, 0, 1, thePlayer)

            if (openInventory)
                mc.netHandler.addToSendQueue(classProvider.createCPacketCloseWindow())

            timer.reset()
        }
    }

}