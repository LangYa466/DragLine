/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.command.commands

import dragline.DragLine
import dragline.event.EventTarget
import dragline.event.Listenable
import dragline.event.Render2DEvent
import dragline.event.UpdateEvent
import dragline.features.command.Command
import dragline.utils.ClientUtils
import dragline.utils.render.RenderUtils

class TacoCommand : Command("taco"), Listenable {
    private var toggle = false
    private var image = 0
    private var running = 0f
    private val tacoTextures = arrayOf(
            classProvider.createResourceLocation("liquidbounce/taco/1.png"),
            classProvider.createResourceLocation("liquidbounce/taco/2.png"),
            classProvider.createResourceLocation("liquidbounce/taco/3.png"),
            classProvider.createResourceLocation("liquidbounce/taco/4.png"),
            classProvider.createResourceLocation("liquidbounce/taco/5.png"),
            classProvider.createResourceLocation("liquidbounce/taco/6.png"),
            classProvider.createResourceLocation("liquidbounce/taco/7.png"),
            classProvider.createResourceLocation("liquidbounce/taco/8.png"),
            classProvider.createResourceLocation("liquidbounce/taco/9.png"),
            classProvider.createResourceLocation("liquidbounce/taco/10.png"),
            classProvider.createResourceLocation("liquidbounce/taco/11.png"),
            classProvider.createResourceLocation("liquidbounce/taco/12.png")
    )

    init {
        DragLine.eventManager.registerListener(this)
    }

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        toggle = !toggle
        ClientUtils.displayChatMessage(if (toggle) "§aTACO TACO TACO. :)" else "§cYou made the little taco sad! :(")
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (!toggle)
            return

        running += 0.15f * RenderUtils.deltaTime
        val scaledResolution = classProvider.createScaledResolution(mc)
        RenderUtils.drawImage(tacoTextures[image], running.toInt(), scaledResolution.scaledHeight - 60, 64, 32)
        if (scaledResolution.scaledWidth <= running)
            running = -64f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!toggle) {
            image = 0
            return
        }

        image++
        if (image >= tacoTextures.size) image = 0
    }

    override fun handleEvents() = true

    override fun tabComplete(args: Array<String>): List<String> {
        return listOf("TACO")
    }
}