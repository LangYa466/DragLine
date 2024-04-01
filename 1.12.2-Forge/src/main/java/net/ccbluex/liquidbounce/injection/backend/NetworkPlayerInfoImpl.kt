/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import com.mojang.authlib.GameProfile
import dragline.api.minecraft.client.network.INetworkPlayerInfo
import dragline.api.minecraft.scoreboard.ITeam
import dragline.api.minecraft.util.IIChatComponent
import dragline.api.minecraft.util.IResourceLocation
import net.minecraft.client.network.NetworkPlayerInfo

class NetworkPlayerInfoImpl(val wrapped: NetworkPlayerInfo) : INetworkPlayerInfo {
    override val locationSkin: IResourceLocation
        get() = wrapped.locationSkin.wrap()
    override val responseTime: Int
        get() = wrapped.responseTime
    override val gameProfile: GameProfile
        get() = wrapped.gameProfile
    override val playerTeam: ITeam?
        get() = wrapped.playerTeam?.wrap()
    override val displayName: IIChatComponent?
        get() = wrapped.displayName?.wrap()

    override fun equals(other: Any?): Boolean {
        return other is NetworkPlayerInfoImpl && other.wrapped == this.wrapped
    }
}

inline fun INetworkPlayerInfo.unwrap(): NetworkPlayerInfo = (this as NetworkPlayerInfoImpl).wrapped
inline fun NetworkPlayerInfo.wrap(): INetworkPlayerInfo = NetworkPlayerInfoImpl(this)