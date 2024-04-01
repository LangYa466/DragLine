/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.combat

import dragline.api.minecraft.client.entity.IEntity
import dragline.event.EventTarget
import dragline.event.Render3DEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.EntityUtils
import dragline.utils.RotationUtils
import dragline.utils.render.RenderUtils
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.ListValue
import java.awt.Color

@ModuleInfo(name = "BowAimbot", description = "Automatically aims at players when using a bow.", category = ModuleCategory.COMBAT)
class BowAimbot : Module() {

    private val silentValue = BoolValue("Silent", true)
    private val predictValue = BoolValue("Predict", true)
    private val throughWallsValue = BoolValue("ThroughWalls", false)
    private val predictSizeValue = FloatValue("PredictSize", 2F, 0.1F, 5F)
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Direction"), "Direction")
    private val markValue = BoolValue("Mark", true)

    private var target: IEntity? = null

    override fun onDisable() {
        target = null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        target = null

        if (classProvider.isItemBow(mc.thePlayer?.itemInUse?.item)) {
            val entity = getTarget(throughWallsValue.get(), priorityValue.get()) ?: return

            target = entity
            RotationUtils.faceBow(target, silentValue.get(), predictValue.get(), predictSizeValue.get())
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (target != null && !priorityValue.get().equals("Multi", ignoreCase = true) && markValue.get())
            RenderUtils.drawPlatform(target, Color(37, 126, 255, 70))
    }

    private fun getTarget(throughWalls: Boolean, priorityMode: String): IEntity? {
        val targets = mc.theWorld!!.loadedEntityList.filter {
            classProvider.isEntityLivingBase(it) && EntityUtils.isSelected(it, true) &&
                    (throughWalls || mc.thePlayer!!.canEntityBeSeen(it))
        }

        return when (priorityMode.toUpperCase()) {
            "DISTANCE" -> targets.minBy { mc.thePlayer!!.getDistanceToEntity(it) }
            "DIRECTION" -> targets.minBy { RotationUtils.getRotationDifference(it) }
            "HEALTH" -> targets.minBy { it.asEntityLivingBase().health }
            else -> null
        }
    }

    fun hasTarget() = target != null && mc.thePlayer!!.canEntityBeSeen(target!!)
}