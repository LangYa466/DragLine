/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.features.module.modules.movement

import dragline.event.*
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.movement.speeds.SpeedMode
import dragline.features.module.modules.movement.speeds.other.*
import dragline.utils.MovementUtils.isMoving
import dragline.utils.misc.MiscUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.ListValue

@ModuleInfo(name = "Speed", description = "Allows you to move faster.", category = ModuleCategory.MOVEMENT)
class Speed : Module() {
    private val speedModes = arrayOf(
        CustomSpeed(),
        Legit(),
        AAC5Fast(),
        VerusHop(),
        VulcanHop(),
        BlocksMCBhop(),
        NewHypixelHop(),
        WatchDog(),
        HytSlowHop(),
        HytFastHop()
    )

    @JvmField
    val modeValue: ListValue = object : ListValue("Mode", modes, "NCPBHop") {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    @JvmField
    val customSpeedValue = FloatValue("CustomSpeed", 1.6f, 0.2f, 2f)

    @JvmField
    val customYValue = FloatValue("CustomY", 0f, 0f, 4f)

    @JvmField
    val customTimerValue = FloatValue("CustomTimer", 1f, 0.1f, 2f)

    @JvmField
    val customStrafeValue = BoolValue("CustomStrafe", true)

    @JvmField
    val resetXZValue = BoolValue("CustomResetXZ", false)

    @JvmField
    val resetYValue = BoolValue("CustomResetY", false)

    @JvmField
    val HypixelTimerValue = FloatValue("Hypixel-MaxTimer", 2.3f, 0.2f, 5f)

    @JvmField
    val HypixelDealyTimerValue = FloatValue("Hypixel-MinTimer", 0.7f, 0.2f, 5f)

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (mc2.player.isSneaking) return
        if (isMoving && (modeValue.get().equals("Legit") && MiscUtils.movingForward())) mc2.player.isSprinting = true
        val speedMode = mode
        speedMode?.onUpdate()
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mc2.player.isSneaking || event.eventState !== EventState.PRE) return
        if (isMoving && (modeValue.get().equals("Legit") && MiscUtils.movingForward())) mc2.player.isSprinting = true
        val speedMode = mode
        speedMode?.onMotion()
    }

    @EventTarget
    fun onMove(event: MoveEvent?) {
        if (mc2.player.isSneaking) return
        val speedMode = mode
        speedMode?.onMove(event)
    }

    @EventTarget
    fun onTick(event: TickEvent?) {
        if (mc2.player.isSneaking) return
        val speedMode = mode
        speedMode?.onTick()
    }

    override fun onEnable() {
        if (mc2.player == null) return
        mc.timer.timerSpeed = 1f
        val speedMode = mode
        speedMode?.onEnable()
    }

    override fun onDisable() {
        if (mc2.player == null) return
        mc.timer.timerSpeed = 1f
        val speedMode = mode
        speedMode?.onDisable()
    }

    override val tag: String
        get() = modeValue.get()
    private val mode: SpeedMode?
        get() {
            val mode = modeValue.get()
            for (speedMode in speedModes) if (speedMode.modeName.equals(mode, ignoreCase = true)) return speedMode
            return null
        }
    private val modes: Array<String>
        get() {
            val list: MutableList<String> = ArrayList()
            for (speedMode in speedModes) list.add(speedMode.modeName)
            return list.toTypedArray<String>()
        }
}
