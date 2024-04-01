/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client.entity

import dragline.api.minecraft.client.network.IINetHandlerPlayClient
import dragline.api.minecraft.util.IIChatComponent
import dragline.api.minecraft.util.IMovementInput

@Suppress("INAPPLICABLE_JVM_NAME")
interface IEntityPlayerSP : IAbstractClientPlayer {
    var horseJumpPowerCounter: Int
    var horseJumpPower: Float

    val sendQueue: IINetHandlerPlayClient
    val movementInput: IMovementInput

    val isHandActive: Boolean

    var serverSprintState: Boolean

    fun sendChatMessage(msg: String)
    fun respawnPlayer()
    fun addChatMessage(component: IIChatComponent)
    fun closeScreen()
}