package me.nelly.module.combat

import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import dragline.utils.MovementUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import me.nelly.utils.PacketUtils
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import java.awt.Toolkit
import java.awt.event.InputEvent

@ModuleInfo(name = "CustomAntiKb", description = "CustomAntiKb", category = ModuleCategory.COMBAT)
class CustomAntiKb : Module() {
    val hurttime = FloatValue("Custom-MinHurtTime", 0f, 0f, 10f)
    val motionX = FloatValue("Custom-HurtMotionX", 0.1f, 0.0f, 1.0f)
    val motionY = FloatValue("Custom-HurtMotionY", 0.1f, 0.0f, 1.0f)
    val motionZ = FloatValue("Custom-HurtMotionZ", 0.1f, 0.0f, 1.0f)
    val onlymove = BoolValue("Custom-OnlyMove",true)
    val onlyground = BoolValue("Custom-OnlyGround",false)
    val hurtcancelpacket = BoolValue("HurtCancelPacket", true)
    val cancels12 = BoolValue("HurtCancelS12", true).displayable { hurtcancelpacket.get() }
    val cancelc0f = BoolValue("HurtCancelC0f", false).displayable { hurtcancelpacket.get() }
    val c0fnotattack = BoolValue("HurtCancelC0f-NotAttack", true).displayable { hurtcancelpacket.get() }
    val cancels32 = BoolValue("HurtCancelS32", false).displayable { hurtcancelpacket.get() }
    val cancels08 = BoolValue("HurtCancelS08", false).displayable { hurtcancelpacket.get() }
    val hurtaddpacket = BoolValue("HurtAddPacket", true)
    val addc07 = BoolValue("HurtAddC07", false).displayable { hurtcancelpacket.get() }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        val packet2 = event.packet
        val packetEntityVelocity = packet2.asSPacketEntityVelocity()
        if (classProvider.isSPacketEntityVelocity(packet2))

        if ((onlymove.get() && !MovementUtils.isMoving) || (onlyground.get() && !mc.thePlayer!!.onGround)) {
            return
        }

        if ((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != mc2.player)
            return

        if(hurtaddpacket.get()) {
            if (addc07.get() && packet is SPacketEntityVelocity) {
                PacketUtils.send(
                    CPacketPlayerDigging(
                        CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        BlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ),
                        EnumFacing.NORTH
                    )
                )
            }
        }

        if (hurtcancelpacket.get()) {

            if (cancels12.get() && packet is SPacketEntityVelocity) {
                event.cancelEvent()
            }

            if (cancelc0f.get() && packet is CPacketConfirmTransaction && !c0fnotattack.get()) {
                event.cancelEvent()
            }

            if (cancels32.get() && packet is SPacketConfirmTransaction) {
                event.cancelEvent()
            }

            if (cancels08.get() && packet is SPacketPlayerPosLook) {
                event.cancelEvent()
            }

        }

        if (mc2.player.hurtTime > hurttime.get()) {
            packetEntityVelocity.motionX = motionX.get().toInt()
            packetEntityVelocity.motionY = motionY.get().toInt()
            packetEntityVelocity.motionZ = motionZ.get().toInt()
        }
    }

    private fun getKbcalculateAverage(): Double {
        return (motionX.get() + motionY.get() + motionZ.get()) / 3.0 * 100
    }

    override val tag: String
        get() = "Knockback:${getKbcalculateAverage().toInt()}%"
}