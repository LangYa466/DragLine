/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.command.commands

import dragline.features.command.Command
import dragline.ui.client.altmanager.GuiAltManager
import dragline.utils.ServerUtils
import dragline.utils.login.MinecraftAccount

class LoginCommand : Command("login") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size <= 1) {
            chatSyntax("login <username/email> [password]")
            return
        }

        val result: String = if (args.size > 2)
            GuiAltManager.login(MinecraftAccount(args[1], args[2]))
        else
            GuiAltManager.login(MinecraftAccount(args[1]))

        chat(result)

        if (result.startsWith("§cYour name is now")) {
            if (mc.isIntegratedServerRunning)
                return

            mc.theWorld!!.sendQuittingDisconnectingPacket()
            ServerUtils.connectToLastServer()
        }
    }
}