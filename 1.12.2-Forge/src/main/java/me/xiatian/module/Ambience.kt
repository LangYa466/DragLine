package me.xiatian.module

import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.FloatValue
import dragline.value.IntegerValue
import dragline.value.ListValue
import net.minecraft.network.play.server.SPacketChangeGameState
import net.minecraft.network.play.server.SPacketTimeUpdate


@ModuleInfo(name = "Ambience", category = ModuleCategory.WORLD, description = "love xiatian233")
class Ambience : Module() {
    private val timeModeValue = ListValue("TimeMode", arrayOf("None", "Normal", "Custom"), "Normal")
    private val weatherModeValue = ListValue("WeatherMode", arrayOf("None", "Sun", "Rain", "Thunder"), "None")
    private val customWorldTimeValue = IntegerValue("CustomTime", 1000, 0, 24000)
    private val changeWorldTimeSpeedValue = IntegerValue("ChangeWorldTimeSpeed", 150, 10, 500)
    private val weatherStrengthValue = FloatValue("WeatherStrength", 1f, 0f, 1f)

    var i = 0L

    override fun onDisable() {
        i = 0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (timeModeValue.get().toLowerCase()) {
            "normal" -> {
                if (i < 24000) {
                    i += changeWorldTimeSpeedValue.get()
                } else {
                    i = 0
                }
                mc2.world.worldTime = i
            }
            "custom" -> {
                mc2.world.worldTime = customWorldTimeValue.get().toLong()
            }
        }

        when (weatherModeValue.get().toLowerCase()) {
            "sun" -> {
                mc2.world.setRainStrength(0f)
                mc2.world.setThunderStrength(0f)
            }
            "rain" -> {
                mc2.world.setRainStrength(weatherStrengthValue.get())
                mc2.world.setThunderStrength(0f)
            }
            "thunder" -> {
                mc2.world.setRainStrength(weatherStrengthValue.get())
                mc2.world.setThunderStrength(weatherStrengthValue.get())
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (!timeModeValue.equals("none") && packet is SPacketTimeUpdate) {
            event.cancelEvent()
        }

        if (!weatherModeValue.equals("none") && packet is SPacketChangeGameState) {
            if (packet.gameState in 7..8) { // change weather packet
                event.cancelEvent()
            }
        }
    }
}
