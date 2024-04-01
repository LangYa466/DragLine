/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import dragline.api.minecraft.client.entity.IEntityPlayerSP
import dragline.api.minecraft.client.network.IINetHandlerPlayClient
import dragline.api.minecraft.util.IIChatComponent
import dragline.api.minecraft.util.IMovementInput
import net.minecraft.client.entity.EntityPlayerSP

open class EntityPlayerSPImpl<T : EntityPlayerSP>(wrapped: T) : AbstractClientPlayerImpl<T>(wrapped), IEntityPlayerSP {
    override var horseJumpPowerCounter: Int
        get() = wrapped.horseJumpPowerCounter
        set(value) {
            wrapped.horseJumpPowerCounter = value
        }
    override var horseJumpPower: Float
        get() = wrapped.horseJumpPower
        set(value) {
            wrapped.horseJumpPower = value
        }
    override val isHandActive: Boolean
        get() = wrapped.isHandActive
    override val sendQueue: IINetHandlerPlayClient
        get() = wrapped.connection.wrap()
    override val movementInput: IMovementInput
        get() = wrapped.movementInput.wrap()
    override val sneaking: Boolean
        get() = wrapped.isSneaking
    override var serverSprintState: Boolean
        get() = wrapped.serverSprintState
        set(value) {
            wrapped.serverSprintState = value
        }

    override fun sendChatMessage(msg: String) = wrapped.sendChatMessage(msg)

    override fun respawnPlayer() = wrapped.respawnPlayer()

    override fun addChatMessage(component: IIChatComponent) = wrapped.sendMessage(component.unwrap())

    override fun closeScreen() = wrapped.closeScreen()
}

inline fun IEntityPlayerSP.unwrap(): EntityPlayerSP = (this as EntityPlayerSPImpl<*>).wrapped
inline fun EntityPlayerSP.wrap(): IEntityPlayerSP = EntityPlayerSPImpl(this)