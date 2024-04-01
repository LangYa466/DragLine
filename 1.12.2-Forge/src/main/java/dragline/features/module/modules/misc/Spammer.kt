/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.misc

import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.misc.RandomUtils.randomString
import dragline.utils.timer.MSTimer
import dragline.utils.timer.TimeUtils
import dragline.value.BoolValue
import dragline.value.IntegerValue
import dragline.value.ListValue
import dragline.value.TextValue
import me.nelly.utils.HttpUtils.get
import java.util.*

@ModuleInfo(name = "Spammer", description = "Spams the chat with a given message.", category = ModuleCategory.MISC)
class Spammer : Module() {
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 4999, 0, 5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelayValueObject = minDelayValue.get()
            if (minDelayValueObject > newValue) set(minDelayValueObject)
            delay = TimeUtils.randomDelay(minDelayValue.get(), this.get())
        }
    }
    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 5000, 0, 5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelayValueObject = maxDelayValue.get()
            if (maxDelayValueObject < newValue) set(maxDelayValueObject)
            delay = TimeUtils.randomDelay(this.get(), maxDelayValue.get())
        }
    }
    private val messageValue =
        TextValue("Message", DragLine.CLIENT_NAME)
    private val customValue = BoolValue("Custom", false)
    private val tiango = BoolValue("TianGoMode", true)
    private val tiango2 = ListValue("TianGo", arrayOf("TianGo1","TianGo2"),"TianGo2")
    private val msTimer = MSTimer()
    private var delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        var tiangostring: String? = null
        if(tiango2.get().equals("TianGo1")) {
            try {
                tiangostring = get("https://v.api.aa1.cn/api/tiangou/index.php")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if(tiango2.get().equals("TianGo2")) {
            try {
                tiangostring = get("https://v.api.aa1.cn/api/api-saohua/index.php?type=text")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (msTimer.hasTimePassed(delay) && !tiango.get()) {
            Objects.requireNonNull(mc.thePlayer)!!
                .sendChatMessage(
                    if (customValue.get()) replace(messageValue.get()) else messageValue.get() + " >" + randomString(
                        5 + Random().nextInt(5)
                    ) + "<"
                )
            msTimer.reset()
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
        } else if (msTimer.hasTimePassed(delay) && tiango.get()) {
                Objects.requireNonNull(mc.thePlayer)!!
                    .sendChatMessage(
                        if (customValue.get()) replace(tiangostring) else "$tiangostring >" + randomString(
                            5 + Random().nextInt(5)
                        ) + "<"
                    )
                msTimer.reset()
                delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            }
        }
    }

    private fun replace(`object`: String?): String {
        var `object` = `object`
        val r = Random()
        while (`object`!!.contains("%f")) `object` =
            `object`.substring(0, `object`.indexOf("%f")) + r.nextFloat() + `object`.substring(
                `object`.indexOf("%f") + "%f".length
            )
        while (`object`!!.contains("%i")) `object` =
            `object`.substring(0, `object`.indexOf("%i")) + r.nextInt(10000) + `object`.substring(
                `object`.indexOf("%i") + "%i".length
            )
        while (`object`!!.contains("%s")) `object` =
            `object`.substring(0, `object`.indexOf("%s")) + randomString(r.nextInt(8) + 1) + `object`.substring(
                `object`.indexOf("%s") + "%s".length
            )
        while (`object`!!.contains("%ss")) `object` =
            `object`.substring(0, `object`.indexOf("%ss")) + randomString(r.nextInt(4) + 1) + `object`.substring(
                `object`.indexOf("%ss") + "%ss".length
            )
        while (`object`!!.contains("%ls")) `object` =
            `object`.substring(0, `object`.indexOf("%ls")) + randomString(r.nextInt(15) + 1) + `object`.substring(
                `object`.indexOf("%ls") + "%ls".length
            )
        return `object`
    }
