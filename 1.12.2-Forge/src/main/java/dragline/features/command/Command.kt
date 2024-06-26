/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.command

import dragline.DragLine
import dragline.utils.ClientUtils
import dragline.utils.MinecraftInstance

abstract class Command(val command: String, vararg val alias: String) : MinecraftInstance() {
    /**
     * Execute commands with provided [args]
     */
    abstract fun execute(args: Array<String>)

    /**
     * Returns a list of command completions based on the provided [args].
     * If a command does not implement [tabComplete] an [EmptyList] is returned by default.
     *
     * @param args an array of command arguments that the player has passed to the command so far
     * @return a list of matching completions for the command the player is trying to autocomplete
     * @author NurMarvin
     */
    open fun tabComplete(args: Array<String>): List<String> {
        return emptyList()
    }

    /**
     * Print [msg] to chat
     */
    protected fun chat(msg: String) = ClientUtils.displayChatMessage("§8[§9§l${DragLine.CLIENT_NAME}§8] §3$msg")

    /**
     * Print [syntax] of command to chat
     */
    protected fun chatSyntax(syntax: String) = ClientUtils.displayChatMessage("§8[§9§l${DragLine.CLIENT_NAME}§8] §3Syntax: §7${DragLine.commandManager.prefix}$syntax")

    /**
     * Print [syntaxes] of command to chat
     */
    protected fun chatSyntax(syntaxes: Array<String>) {
        ClientUtils.displayChatMessage("§8[§9§l${DragLine.CLIENT_NAME}§8] §3Syntax:")

        for (syntax in syntaxes)
            ClientUtils.displayChatMessage("§8> §7${DragLine.commandManager.prefix}$command ${syntax.toLowerCase()}")
    }

    /**
     * Print a syntax error to chat
     */
    protected fun chatSyntaxError() = ClientUtils.displayChatMessage("§8[§9§l${DragLine.CLIENT_NAME}§8] §3Syntax error")

    /**
     * Play edit sound
     */
    protected fun playEdit() = mc.soundHandler.playSound("random.anvil_use", 1F)
}