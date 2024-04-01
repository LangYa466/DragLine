/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.world


import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.BoolValue
import dragline.value.IntegerValue


@ModuleInfo(
    name = "FastPlace",
    description = "Allows you to place blocks faster.",
    category = ModuleCategory.WORLD
)
class FastPlace : Module() {
    var pressing = false
    val speedValue = IntegerValue("Speed", 0, 0, 4)
    val hytBypass = BoolValue("HytBypass", true)
    val onlyBlock = BoolValue("OnlyBlock", true)

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if (onlyBlock.get() && !classProvider.isItemBlock(mc.thePlayer!!.heldItem!!.item)) speedValue.set(1) else speedValue.set(0)
        if (hytBypass.get()) {
            if (pressing) speedValue.set(1) else speedValue.set(2)
        }
        if (!pressing && mc.gameSettings.keyBindUseItem.pressed) pressing = true
        if (pressing && !mc.gameSettings.keyBindUseItem.pressed) pressing = false
    }


}
