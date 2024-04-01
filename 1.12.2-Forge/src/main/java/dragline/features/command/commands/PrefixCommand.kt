/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.command.commands

import dragline.DragLine
import dragline.features.command.Command

class PrefixCommand : Command("prefix") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size <= 1) {
            chatSyntax("prefix <character>")
            return
        }

        val prefix = args[1]

        if (prefix.length > 1) {
            chat("§cPrefix can only be one character long!")
            return
        }

        DragLine.commandManager.prefix = prefix.single()
        DragLine.fileManager.saveConfig(DragLine.fileManager.valuesConfig)

        chat("Successfully changed command prefix to '§8$prefix§3'")
    }
}