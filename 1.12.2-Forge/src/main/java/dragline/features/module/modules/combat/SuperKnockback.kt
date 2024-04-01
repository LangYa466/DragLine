/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package dragline.features.module.modules.combat

import me.nelly.utils.PacketUtils
import dragline.event.AttackEvent
import dragline.event.EventTarget
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.timer.MSTimer
//import dragline.value.BoolValue
import dragline.value.IntegerValue
import net.minecraft.network.play.client.CPacketEntityAction

@ModuleInfo(name = "MoreKb", category = ModuleCategory.COMBAT, description = "Increases knockback dealt to other entities.")
class SuperKnockback : Module() {
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val delayValue = IntegerValue("Delay", 0, 0, 500)

    val timer = MSTimer()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (classProvider.isEntityLivingBase(event.targetEntity)) {
            if (event.targetEntity!!.asEntityLivingBase().hurtTime > hurtTimeValue.get() || !timer.hasTimePassed(delayValue.get().toLong())) {
                return
            }

            val player = mc2.player!!
            if (player.serverSprintState)
                PacketUtils.send(CPacketEntityAction(player, CPacketEntityAction.Action.STOP_SPRINTING))

            PacketUtils.send(CPacketEntityAction(player, CPacketEntityAction.Action.START_SPRINTING))
            player.serverSprintState = true
            timer.reset()
        }
    }

}
