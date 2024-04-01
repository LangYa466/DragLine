/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.features.module.modules.client

import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.ListValue
import org.apache.commons.lang3.RandomUtils

@ModuleInfo(name = "FakeAntiAim" ,description = "OMG!!!", category = ModuleCategory.MISC)
class FakeAntiAim : Module() {
    private val yawMode = ListValue("YawMove", arrayOf("Jitter", "Spin", "Back", "BackJitter"), "Spin")

    private var yaw = 0f

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (yawMode.get().toLowerCase()) {
            "spin" -> {
                yaw += 20.0f
                if (yaw > 180.0f) {
                    yaw = -180.0f
                } else if (yaw < -180.0f) {
                    yaw = 180.0f
                }
            }

            "jitter" -> {
                yaw = mc.thePlayer!!.rotationYaw + if (mc.thePlayer!!.ticksExisted % 2 == 0) 90F else -90F
            }

            "back" -> {
                yaw = mc.thePlayer!!.rotationYaw + 180f
            }

            "backjitter" -> {
                yaw = mc.thePlayer!!.rotationYaw + 180f + RandomUtils.nextDouble(-3.0, 3.0).toFloat()
            }
        }

        mc2.player.renderYawOffset = yaw
        mc2.player.rotationYawHead = yaw

    }
}