/*
 * DragLine Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/DragLine/
 */

package dragline.script.api.global

import dragline.utils.ClientUtils

/**
 * Object used by the script API to provide an easier way of calling chat-related methods.
 */
object Chat {

    /**
     * Prints a message to the chat (client-side)
     * @param message Message to be printed
     */
    @Suppress("unused")
    @JvmStatic
    fun print(message: String) {
        ClientUtils.displayChatMessage(message)
    }
}