package me.nelly.module.player

import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.UpdateEvent
import dragline.event.WorldEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.timer.TimeUtils
import dragline.value.BoolValue
import me.nelly.utils.PacketUtils
import nellyobfuscator.NellyClassObfuscator
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock


@Native
@RegisterLock
@NellyClassObfuscator
@ModuleInfo(name = "Disabler", description = "Disable AntiCheat", category = ModuleCategory.EXPLOIT)
class Disabler : Module() {
    var postValue = BoolValue("Grim-Post", false)
    var badPacketsA = BoolValue("Grim-BadPacketsA", false)
    var badPacketsF = BoolValue("Grim-BadPacketsF", false)
    var lastSlot = -1
    var startChiJiDisabler = false
    var lastSprinting = false

    override val tag: String
        get() = "Grim"

    @EventTarget
    fun onWorld(event: WorldEvent?) {
        lastSlot = -1
        lastSprinting = false
        startChiJiDisabler = false
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (mc.thePlayer != null) {
            if (!mc.thePlayer!!.isDead) {
                if (badPacketsF.get() && packet is CPacketEntityAction) {
                    if ((packet as CPacketEntityAction).getAction() === CPacketEntityAction.Action.START_SPRINTING) {
                        if (lastSprinting) {
                            event.cancelEvent()
                        }
                        lastSprinting = true
                    } else if ((packet as CPacketEntityAction).getAction() === CPacketEntityAction.Action.STOP_SPRINTING) {
                        if (!lastSprinting) {
                            event.cancelEvent()
                        }
                        lastSprinting = false
                    }
                }
                if (badPacketsA.get() && packet is CPacketHeldItemChange) {
                    val slot: Int = (packet as CPacketHeldItemChange).getSlotId()
                    if (slot == lastSlot && slot != -1) {
                        event.cancelEvent()
                    }
                    lastSlot = (packet as CPacketHeldItemChange).getSlotId()
                }
                if (postValue.get()) {
                    if (packet is CPacketAnimation) {
                        sendPacketC0F()
                    }
                    if (packet is CPacketPlayerAbilities) {
                        sendPacketC0F()
                    }
                    /*
                    if (packet is C08PacketPlayerBlockPlacement) {
                        sendPacketC0F()
                    }
                     */
                    if (packet is CPacketPlayerDigging) {
                        sendPacketC0F()
                    }
                    if (packet is CPacketUseEntity) {
                        sendPacketC0F()
                    }
                    if (packet is CPacketClickWindow) {
                        sendPacketC0F()
                    }
                    if (packet is CPacketEntityAction) {
                        sendPacketC0F()
                    }
                }
            }
        }
    }
}

fun sendPacketC0F() {
    PacketUtils.send(CPacketConfirmTransaction(Int.MAX_VALUE, 32767.toShort(), true))
}


