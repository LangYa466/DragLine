/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.command.commands

import dragline.features.command.Command

class VClipCommand : Command("vclip") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            try {
                val y = args[1].toDouble()
                val thePlayer = mc.thePlayer ?: return

                val entity = if (thePlayer.isRiding) thePlayer.ridingEntity!! else thePlayer

                entity.setPosition(entity.posX, entity.posY + y, entity.posZ)
                chat("You were teleported.")
            } catch (ex: NumberFormatException) {
                chatSyntaxError()
            }

            return
        }

        chatSyntax("vclip <value>")
    }
}
