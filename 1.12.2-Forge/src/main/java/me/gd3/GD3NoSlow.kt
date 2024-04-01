package me.gd3

import dragline.DragLine
import dragline.event.*
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.exploit.KeepContainer
import dragline.value.BoolValue
import nellyobfuscator.NellyClassObfuscator
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.minecraft.block.Block
import net.minecraft.inventory.ClickType
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.client.CPacketClickWindow
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.network.play.server.SPacketSetSlot
import net.minecraft.network.play.server.SPacketWindowItems
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock
import java.util.*

@RegisterLock
@Native
@NellyClassObfuscator
@ModuleInfo(name = "GD3NoSlow", description = "by gd3", category = ModuleCategory.MOVEMENT)
class GD3NoSlow : Module() {

    private val slowvalue = BoolValue("Slow",true)
    private val abslow = BoolValue("ABSlow",true)
    private var packetBuf = LinkedList<Packet<INetHandlerPlayClient>>()
    private var slow = false

    override fun onEnable() {
        slow = false
    }

    override fun onDisable() {
        blink()
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        val stack = mc.thePlayer?.unwrap()?.activeItemStack ?: return
        if (stack == ItemStack.EMPTY) {
            blink()
        }
        if (slow) { // 不知道为什么触发，但是保险起见
            flush_inv()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val pw = event.packet.unwrap()
        if (pw is CPacketPlayerTryUseItem) {
            val stack = mc.thePlayer?.unwrap()?.getHeldItem(pw.hand)?.item ?: return
            if (stack is ItemSword || stack is ItemFood || stack is ItemPotion || stack is ItemBucketMilk || stack is ItemBow || stack is ItemShield) {
                slow = true
                flush_inv()
            }
        }
    }

    @EventTarget
    fun onPacketProcess(event: PacketProcessEvent) {
        val pw = event.packet.unwrap()
        if (pw is SPacketWindowItems && pw.windowId == getmaxwindowId()) {
            if ((mc.thePlayer?.unwrap()?.activeItemStack ?: ItemStack.EMPTY) != ItemStack.EMPTY) {
                packetBuf.add(pw)
                event.cancelEvent()
            }
            slow = false
        } else if (pw is SPacketSetSlot && (pw.windowId == getmaxwindowId() || pw.windowId == -1 || pw.windowId == -2) && (mc.thePlayer?.unwrap()?.activeItemStack ?: ItemStack.EMPTY) != ItemStack.EMPTY) {
            packetBuf.add(pw)
            event.cancelEvent()
        } else if (pw is SPacketConfirmTransaction && pw.windowId == getmaxwindowId() && pw.actionNumber != 11451.toShort() && slow) {
            val connection = mc.unwrap().connection ?: return
            connection.sendPacket(CPacketConfirmTransaction(0, 11451.toShort(), true))
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        if (slow && slowvalue.get()) {
            event.forward = 0.2f
            event.strafe = 0.2f
        } else {
            event.forward = 1f
            event.strafe = 1f
        }

        if(mc.thePlayer!!.isBlocking && abslow.get()) {
            event.forward = 0.2f
            event.strafe = 0.2f
        }

    }

    fun blink() {
        val connection = mc.unwrap().connection ?: return
        for (i in packetBuf)
            i.processPacket(connection)
        packetBuf.clear()
    }

    fun flush_inv() {
        val connection = mc.unwrap().connection ?: return
        if (this.windowId == getmaxwindowId()) {
            connection.sendPacket(CPacketClickWindow(0, 36, 0, ClickType.SWAP, ItemStack(Block.getBlockById(166)), 11451.toShort()))
        }
    }

    val windowId: Int
        get() {
            var id = 0
            val keepContainerModule = DragLine.moduleManager.getModule(KeepContainer::class.java) as KeepContainer
            if (keepContainerModule.container != null && keepContainerModule.container!!.inventorySlots != null) {
                id = keepContainerModule.container!!.inventorySlots!!.windowId
            } else if (mc.thePlayer != null && mc.thePlayer!!.openContainer != null) {
                id = mc.thePlayer!!.openContainer!!.windowId
            }
            return id
        }
    
    fun getmaxwindowId(): Int {
        return 0
    }

}