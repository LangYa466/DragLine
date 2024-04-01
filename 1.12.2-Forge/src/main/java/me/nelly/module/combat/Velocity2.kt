package me.nelly.module.combat

import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.UpdateEvent
import dragline.event.WorldEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.BoolValue
import me.nelly.utils.PacketUtils
import nellyobfuscator.NellyClassObfuscator
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketExplosion
import net.minecraft.util.EnumFacing
import net.minecraft.util.Timer
import net.minecraft.util.math.BlockPos

@NellyClassObfuscator
@ModuleInfo(name = "Velocity2", description = "Nel1y", category = ModuleCategory.COMBAT)
class Velocity2 : Module() {

    val packet2 = BoolValue("Packet",true)
    val faketimer = BoolValue("FakeTimer",true)
    val c07 = BoolValue("C07",true)

    var shouldCancel = false
    var flags = 0

    @EventTarget
    fun onPacket(event : PacketEvent) {
        val packet = event.packet.unwrap()
        if (packet2.get()) {
            if (packet is SPacketEntityVelocity && mc.thePlayer!!.hurtTime > 0) {
                event.cancelEvent()
                shouldCancel = true
            }
            if (packet is SPacketExplosion) {
                event.cancelEvent()
                shouldCancel = true
            }
        }
    }

    @EventTarget
    fun onUpdate(event:UpdateEvent) {
        if (shouldCancel && flags == 0 && mc.thePlayer!!.hurtTime > 0) {
            sendPacketC0F()
            val pos = BlockPos(mc2.player)
            if (faketimer.get()) {
                var var10000: Timer = mc2.timer
                var10000.lastSyncSysClock += 60L
                ++mc2.player.positionUpdateTicks
                var10000 = mc2.timer
                var10000.elapsedPartialTicks -= 0.8f
                PacketUtils.send(CPacketPlayer(mc.thePlayer!!.onGround))
            }
            if (c07.get()) {
                PacketUtils.send(CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, pos, EnumFacing.DOWN))
                PacketUtils.send(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN))
                shouldCancel = false
            }
        }
    }

    @EventTarget
    fun onWorld(event:WorldEvent) {
        shouldCancel = false
    }

    override fun onEnable() {
        shouldCancel = false
        super.onEnable()
    }

    override fun onDisable() {

        if (packet2.get()) {
            if(mc!!.thePlayer !=null && mc.netHandler != null && mc.theWorld != null)
            sendPacketC0F()
            PacketUtils.send(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                    BlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ),
                    EnumFacing.DOWN
                )
            )
            PacketUtils.send(
                CPacketPlayerDigging(
                    CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    BlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY + 1.11, mc.thePlayer!!.posZ),
                    EnumFacing.DOWN
                )
            )
        }
        shouldCancel = false

        super.onDisable()
    }

    fun sendPacketC0F() {
        PacketUtils.send(CPacketConfirmTransaction(Int.MAX_VALUE, 32767.toShort(), true))
    }

}