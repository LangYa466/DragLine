/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.misc

import dragline.api.minecraft.client.entity.IEntityLivingBase
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.BoolValue

@ModuleInfo(name = "Teams", description = "Prevents Killaura from attacking team mates.", category = ModuleCategory.MISC)
class Teams : Module() {

    private val scoreboardValue = BoolValue("ScoreboardTeam", true)
    private val colorValue = BoolValue("Color", true)
    private val gommeSWValue = BoolValue("GommeSW", false)
    private val armorColorValue = BoolValue("ArmorColor", false)

    /**
     * Check if [entity] is in your own team using scoreboard, name color or team prefix
     */
    fun isInYourTeam(entity: IEntityLivingBase): Boolean {
        val thePlayer = mc.thePlayer ?: return false

        if (scoreboardValue.get() && thePlayer.team != null && entity.team != null &&
                thePlayer.team!!.isSameTeam(entity.team!!))
            return true

        val displayName = thePlayer.displayName

        if(armorColorValue.get()){
            val entityPlayer = entity.asEntityPlayer()
            if(thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null){
                val myHead = thePlayer.inventory.armorInventory[3]
                val myItemArmor = myHead!!.item!!.asItemArmor()


                val entityHead = entityPlayer.inventory.armorInventory[3]
                var entityItemArmor = myHead.item!!.asItemArmor()

                if(myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead!!)){
                    return true
                }
            }
        }

        if (gommeSWValue.get() && displayName != null && entity.displayName != null) {
            val targetName = entity.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            if (targetName.startsWith("T") && clientName.startsWith("T"))
                if (targetName[1].isDigit() && clientName[1].isDigit())
                    return targetName[1] == clientName[1]
        }

        if (colorValue.get() && displayName != null && entity.displayName != null) {
            val targetName = entity.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            return targetName.startsWith("§${clientName[1]}")
        }

        return false
    }

}
