/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */
package me.nelly.module.misc

import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.BoolValue
import dragline.value.TextValue
import org.lwjgl.opengl.Display.setTitle

@ModuleInfo(name = "Title", description = "Title", category = ModuleCategory.MISC)
class Title : Module() {
    private val mainTitle = TextValue("FirstTitle", DragLine.CLIENT_NAME)
    private val midTitle = TextValue("MiddleTitle", DragLine.CLIENT_VERSION.toString())
    private val showPlayTime = BoolValue("ShowPlayTime", true)
    private val showYiYan = BoolValue("showYiYan", true)
    private var ticks = 0
    private var seconds = 0
    private var minutes = 0
    private var hours = 0

    override fun onDisable() {
        setTitle("${DragLine.CLIENT_NAME} ${DragLine.CLIENT_VERSION}")
        super.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        ticks++
        if (ticks == 20) {
            seconds++
            ticks = 0
        }
        if (seconds == 60) {
            minutes++
            seconds = 0
        }
        if (minutes == 60) {
            hours++
            minutes = 0
        }

        setTitle(mainTitle.get() + " | " + midTitle.get() + " | " + if (showPlayTime.get()) "$hours 时 $minutes 分 $seconds 秒 " else "" + " | " +  if (showYiYan.get()) " | ${DragLine.yiyan}" else "")
    }
}