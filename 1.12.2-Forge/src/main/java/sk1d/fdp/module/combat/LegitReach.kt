/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package sk1d.fdp.module.combat

import dragline.event.*
import dragline.features.module.*
import dragline.utils.*
import dragline.utils.timer.MSTimer
import dragline.value.*
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.Packet
import net.minecraft.network.ThreadQuickExitException
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.*
import net.minecraft.network.play.INetHandlerPlayClient
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

@ModuleInfo(name = "LegitReach", description="skid from fdp", category = ModuleCategory.COMBAT)
class LegitReach : Module() {

    private val pulseDelayValue = IntegerValue("PulseDelay", 200, 50, 500)
    
    private val pulseTimer = MSTimer()
    var currentTarget: EntityLivingBase? = null
    
    private val packets = LinkedBlockingQueue<Packet<INetHandlerPlayClient>>()


    private val _lastAttackTimer = MSTimer()

    private var _inCombat = false
    private var _target: EntityLivingBase? = null


    override fun onDisable() {
        removeFakePlayer()
        synchronized (this) {
            thread {
                clearPackets()
            }.join()
        }
    }

    private fun removeFakePlayer() {
        currentTarget = null
    }


    private fun clearPackets() = clearPackets(false)

    private fun clearPackets(now: Boolean) {
        while (!packets.isEmpty()) {
            val packetIn = packets.take() ?: continue
            val scheduler = mc.unwrap()
            val processor = scheduler.connection ?: continue
            if (now) {
                try {
                    packetIn.processPacket(processor)
                } catch (ignored: ThreadQuickExitException) {

                }
            } else {
                scheduler.addScheduledTask(Runnable()
                {
                    packetIn.processPacket(processor)
                })
            }
        }
    }


    @EventTarget
    fun onAttack(event: AttackEvent) {
        manager_onAttack(event)
        if (event.targetEntity == null) return
        if (currentTarget == null || event.targetEntity.unwrap() != currentTarget!!) {
            currentTarget = event.targetEntity.unwrap() as EntityLivingBase?
            synchronized (this) {
                thread {
                    clearPackets()
                }.join()
            }
        }
    }

    @EventTarget
    fun onUpdate(@Suppress("UNUSED_PARAMETER") event: UpdateEvent?) {
        manager_onUpdate()
        if (!_inCombat) {
            currentTarget = null
        }
        if (pulseTimer.hasTimePassed(pulseDelayValue.get().toLong())) {
            pulseTimer.reset()
            synchronized (this) {
                thread {
                    clearPackets()
                }.join()
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (!packet.javaClass.name.startsWith("net.minecraft.network.play.server.S")) return
        synchronized (this) {
            if (packet is SPacketChat
            || packet is SPacketUpdateHealth
            || packet is SPacketScoreboardObjective
            || packet is SPacketUpdateScore
            || packet is SPacketAnimation
            || packet is SPacketEntityEquipment
            || packet is SPacketEntityHeadLook
            || packet is SPacketEntityMetadata
            || packet is SPacketEntityProperties
            || packet is SPacketEntityStatus
            || packet is SPacketCustomSound
            || packet is SPacketSoundEffect) return
            if (packet is SPacketDisconnect) {
                packets.clear()
                return
            }
            if (_inCombat) {
                if ((mc.thePlayer?.ticksExisted ?: 0) < 20) return
                event.cancelEvent()
                packets.add(packet as Packet<INetHandlerPlayClient>)
            } else {
                clearPackets(true)
            }
            if (packet is SPacketEntityVelocity && packet.entityID == (mc.thePlayer?.entityId ?: -1337)) {
                clearPackets(true)
            } else if (packet is SPacketExplosion && (packet.motionX != 0f || packet.motionY != 0f || packet.motionZ != 0f)) {
                clearPackets(true)
            } else if (packet is SPacketPlayerPosLook) {
                clearPackets(true)
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        _inCombat = false
        _target = null
    }

    private fun manager_onUpdate() {
        val thePlayer = mc.thePlayer ?: return

        _inCombat = false

        if (!_lastAttackTimer.hasTimePassed(250)) {
            _inCombat = true
            return
        }

        if (_target != null) {
            if (thePlayer.unwrap().getDistance(_target!!) > 7 || !_inCombat || _target!!.isDead) {
                _target = null
            } else {
                _inCombat = true
            }
        }
    }

    private fun manager_onAttack(event: AttackEvent) {
        val target = event.targetEntity ?: return

        if (target.unwrap() is EntityLivingBase && EntityUtils.isSelected(target, true)) {
            _target = target.unwrap() as EntityLivingBase
            _lastAttackTimer.reset()
        }
    }
}