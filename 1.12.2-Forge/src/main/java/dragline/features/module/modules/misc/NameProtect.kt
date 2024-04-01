/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.misc

import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.TextEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.misc.StringUtils
import dragline.utils.render.ColorUtils.translateAlternateColorCodes
import dragline.value.BoolValue
import dragline.value.TextValue

@ModuleInfo(name = "NameProtect", description = "Changes playernames clientside.", category = ModuleCategory.MISC)
class NameProtect : Module() {
    @JvmField
    val allPlayersValue = BoolValue("AllPlayers", false)
    private val elseplayernameValue = TextValue("AllPlayersFakeName", "Protected User")

    @JvmField
    val skinProtectValue = BoolValue("SkinProtect", true)
    private val fakeNameValue = TextValue("FakeName", "&cMe")

    @EventTarget(ignoreCondition = true)
    fun onText(event: TextEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || event.text!!.contains("§8[§9§l" + DragLine.CLIENT_NAME + "§8] §3"))
            return

        for (friend in DragLine.fileManager.friendsConfig.friends)
            event.text = StringUtils.replace(event.text, friend.playerName, translateAlternateColorCodes(friend.alias) + "§f")

        if (!state)
            return
        event.text = StringUtils.replace(event.text, thePlayer.name, translateAlternateColorCodes(fakeNameValue.get()) + "§f")

        if (allPlayersValue.get()) {
            for (playerInfo in mc.netHandler.playerInfoMap)
                event.text = StringUtils.replace(event.text, playerInfo.gameProfile.name, elseplayernameValue.get())
        }

        val thePlayer2 = mc2.player

        if (thePlayer2 == null || event.text!!.contains("§8[§9§l" + DragLine.CLIENT_NAME + "§8] §3"))
            return

        for (friend in DragLine.fileManager.friendsConfig.friends)
            event.text = StringUtils.replace(event.text, friend.playerName, translateAlternateColorCodes(friend.alias) + "§f")

        if (!state)
            return
        event.text = StringUtils.replace(event.text, thePlayer2.name, translateAlternateColorCodes(fakeNameValue.get()) + "§f")

        if (allPlayersValue.get()) {
            for (playerInfo in mc.netHandler.playerInfoMap)
                event.text = StringUtils.replace(event.text, playerInfo.gameProfile.name, "Protected User")
        }
    }
}