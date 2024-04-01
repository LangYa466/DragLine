/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.movement

import dragline.api.minecraft.network.play.client.ICPacketEntityAction
import dragline.event.EventState
import dragline.event.EventTarget
import dragline.event.MotionEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.MovementUtils
import dragline.value.BoolValue
import dragline.value.ListValue

@ModuleInfo(name = "Sneak", description = "Automatically sneaks all the time.", category = ModuleCategory.MOVEMENT)
class Sneak : Module() {
    @JvmField
    val modeValue = ListValue("Mode", arrayOf("Legit", "Vanilla", "Switch", "MineSecure"), "MineSecure")

    @JvmField
    val stopMoveValue = BoolValue("StopMove", false)
    private var sneaked = false
    override fun onEnable() {
        val thePlayer = mc.thePlayer ?: return

        if ("vanilla".equals(modeValue.get(), ignoreCase = true)) {
            mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(thePlayer, ICPacketEntityAction.WAction.START_SNEAKING))
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (stopMoveValue.get() && MovementUtils.isMoving) {
            if (sneaked) {
                onDisable()
                sneaked = false
            }
            return
        }
        sneaked = true
        when (modeValue.get().toLowerCase()) {
            "legit" -> mc.gameSettings.keyBindSneak.pressed = true
            "switch" -> {
                when (event.eventState) {
                    EventState.PRE -> {
                        if (!MovementUtils.isMoving) return
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SNEAKING))
                    }
                    EventState.POST -> {
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SNEAKING))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                    }
                }
            }
            "minesecure" -> {
                if (event.eventState == EventState.PRE)
                    return

                mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
            }
        }
    }

    override fun onDisable() {
        val player = mc.thePlayer ?: return

        when (modeValue.get().toLowerCase()) {
            "legit" -> {
                if (!mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
                    mc.gameSettings.keyBindSneak.pressed = false
                }
            }
            "vanilla", "switch", "minesecure" -> {
                mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(player, ICPacketEntityAction.WAction.STOP_SNEAKING))
            }
        }
        super.onDisable()
    }
}