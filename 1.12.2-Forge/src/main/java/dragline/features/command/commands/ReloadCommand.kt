/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.command.commands

import dragline.DragLine
import dragline.features.command.Command
import dragline.features.command.CommandManager
import dragline.ui.client.clickgui.ClickGui
import dragline.ui.font.Fonts

class ReloadCommand : Command("reload", "configreload") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        chat("Reloading...")
        chat("§c§lReloading commands...")
        DragLine.commandManager = CommandManager()
        DragLine.commandManager.registerCommands()
        DragLine.isStarting = true
        DragLine.scriptManager.disableScripts()
        DragLine.scriptManager.unloadScripts()
        for(module in DragLine.moduleManager.modules)
            DragLine.moduleManager.generateCommand(module)
        chat("§c§lReloading scripts...")
        DragLine.scriptManager.loadScripts()
        DragLine.scriptManager.enableScripts()
        chat("§c§lReloading fonts...")
        Fonts.loadFonts()
        chat("§c§lReloading modules...")
        DragLine.fileManager.loadConfig(DragLine.fileManager.modulesConfig)
        DragLine.isStarting = false
        chat("§c§lReloading values...")
        DragLine.fileManager.loadConfig(DragLine.fileManager.valuesConfig)
        chat("§c§lReloading accounts...")
        DragLine.fileManager.loadConfig(DragLine.fileManager.accountsConfig)
        chat("§c§lReloading friends...")
        DragLine.fileManager.loadConfig(DragLine.fileManager.friendsConfig)
        chat("§c§lReloading xray...")
        DragLine.fileManager.loadConfig(DragLine.fileManager.xrayConfig)
        chat("§c§lReloading HUD...")
        DragLine.fileManager.loadConfig(DragLine.fileManager.hudConfig)
        chat("§c§lReloading ClickGUI...")
        DragLine.clickGui = ClickGui()
        DragLine.fileManager.loadConfig(DragLine.fileManager.clickGuiConfig)
        chat("Reloaded.")
    }
}
