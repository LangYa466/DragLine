package dragline.features.module.modules.combat

import dragline.api.minecraft.client.settings.IKeyBinding
import dragline.api.minecraft.item.IItemStack
import dragline.api.minecraft.potion.PotionType
import dragline.event.EventTarget
import dragline.event.MotionEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.timer.TimeUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue
import org.lwjgl.input.Mouse

@ModuleInfo(name = "AutoHead", description = "faq", category = ModuleCategory.COMBAT)
class AutoHead : Module() {
    private var eatingApple = false
    private var switched = -1
    var doingStuff = false
    private val timer = TimeUtils()
    private val eatHeads = BoolValue("EatHead", true)
    private val eatApples = BoolValue("EatApples", true)
    private val health = FloatValue("Health", 10.0f, 1.0f, 20.0f)
    private val delay = IntegerValue("Delay", 750, 100, 2000)

    override fun onEnable() {
        doingStuff = false
        eatingApple = doingStuff
        switched = -1
        timer.reset()
        super.onEnable()
    }

    override fun onDisable() {
        doingStuff = false
        if (eatingApple) {
            repairItemPress()
            repairItemSwitch()
        }
        super.onDisable()
    }

    private fun repairItemPress() {
        if (mc.gameSettings != null) {
            val keyBindUseItem: IKeyBinding = mc.gameSettings.keyBindUseItem
            if (keyBindUseItem != null) keyBindUseItem.pressed = false
        }
    }

    @EventTarget
    fun onUpdate(event: MotionEvent?) {
        if (mc.thePlayer == null) return
        val inventory = mc.thePlayer!!.inventory ?: return
        doingStuff = false
        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            val useItem: IKeyBinding = mc.gameSettings.keyBindUseItem
            if (!timer.hasReached(delay.get().toDouble())) {
                eatingApple = false
                repairItemPress()
                repairItemSwitch()
                return
            }
            if (mc.thePlayer!!.capabilities.isCreativeMode || mc.thePlayer!!.isPotionActive(classProvider.getPotionEnum(
                    PotionType.REGENERATION)) || mc.thePlayer!!.health >= health.get()) {
                timer.reset()
                if (eatingApple) {
                    eatingApple = false
                    repairItemPress()
                    repairItemSwitch()
                }
                return
            }
            for (i in 0..1) {
                val doEatHeads = i != 0
                if (doEatHeads) {
                    if (!eatHeads.get()) continue
                } else {
                    if (!eatApples.get()) {
                        eatingApple = false
                        repairItemPress()
                        repairItemSwitch()
                        continue
                    }
                }
                var slot: Int
                slot = if (doEatHeads) {
                    getItemFromHotbar(397)
                } else {
                    getItemFromHotbar(322)
                }
                if (slot == -1) continue
                val tempSlot = inventory.currentItem
                doingStuff = true
                if (doEatHeads) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(slot))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerBlockPlacement(mc.thePlayer!!.inventory.getCurrentItemInHand()))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(tempSlot))
                    timer.reset()
                } else {
                    inventory.currentItem = slot
                    useItem.pressed = true
                    if (eatingApple) continue  // no message spam
                    eatingApple = true
                    switched = tempSlot
                }
            }
        }
    }

    private fun repairItemSwitch() {
        val p = mc.thePlayer ?: return
        val inventory = p.inventory ?: return
        var switched = switched
        if (switched == -1) return
        inventory.currentItem = switched
        switched = -1
        this.switched = switched
    }

    private fun getItemFromHotbar(id: Int): Int {
        for (i in 0..8) {
            if (mc.thePlayer!!.inventory.mainInventory[i] != null) {
                val a: IItemStack? = mc.thePlayer!!.inventory.mainInventory[i]
                val item = a!!.item
                if (functions.getIdFromItem(item!!) == id) {
                    return i
                }
            }
        }
        return -1
    }
}