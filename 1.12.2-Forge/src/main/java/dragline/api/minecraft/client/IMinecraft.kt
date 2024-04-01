/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package dragline.api.minecraft.client

import dragline.api.minecraft.client.audio.ISoundHandler
import dragline.api.minecraft.client.entity.IEntity
import dragline.api.minecraft.client.entity.IEntityPlayerSP
import dragline.api.minecraft.client.gui.IFontRenderer
import dragline.api.minecraft.client.gui.IGuiScreen
import dragline.api.minecraft.client.multiplayer.IPlayerControllerMP
import dragline.api.minecraft.client.multiplayer.IServerData
import dragline.api.minecraft.client.multiplayer.IWorldClient
import dragline.api.minecraft.client.network.IINetHandlerPlayClient
import dragline.api.minecraft.client.render.entity.IRenderItem
import dragline.api.minecraft.client.render.texture.ITextureManager
import dragline.api.minecraft.client.renderer.IEntityRenderer
import dragline.api.minecraft.client.renderer.IRenderGlobal
import dragline.api.minecraft.client.settings.IGameSettings
import dragline.api.minecraft.client.shader.IFramebuffer
import dragline.api.minecraft.renderer.entity.IRenderManager
import dragline.api.minecraft.util.IMovingObjectPosition
import dragline.api.minecraft.util.ISession
import dragline.api.minecraft.util.ITimer
import java.io.File

interface IMinecraft {
    val framebuffer: IFramebuffer
    val isFullScreen: Boolean
    val dataDir: File
    val debugFPS: Int
    val renderGlobal: IRenderGlobal
    val renderItem: IRenderItem
    val displayWidth: Int
    val displayHeight: Int
    val entityRenderer: IEntityRenderer
    var rightClickDelayTimer: Int
    var session: ISession
    val soundHandler: ISoundHandler
    val objectMouseOver: IMovingObjectPosition?
    val timer: ITimer
    val renderManager: IRenderManager
    val playerController: IPlayerControllerMP
    val currentScreen: IGuiScreen?
    var renderViewEntity: IEntity?
    val netHandler: IINetHandlerPlayClient
    val theWorld: IWorldClient?
    val thePlayer: IEntityPlayerSP?
    val textureManager: ITextureManager
    val isIntegratedServerRunning: Boolean
    val currentServerData: IServerData?
    val gameSettings: IGameSettings
    val fontRendererObj: IFontRenderer

    fun displayGuiScreen(screen: IGuiScreen?)
    fun rightClickMouse()
    fun shutdown()
    fun toggleFullscreen()
}