/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client.network

import com.mojang.authlib.GameProfile
import dragline.api.minecraft.scoreboard.ITeam
import dragline.api.minecraft.util.IIChatComponent
import dragline.api.minecraft.util.IResourceLocation

interface INetworkPlayerInfo {
    val locationSkin: IResourceLocation
    val responseTime: Int
    val gameProfile: GameProfile
    val playerTeam: ITeam?
    val displayName: IIChatComponent?
}