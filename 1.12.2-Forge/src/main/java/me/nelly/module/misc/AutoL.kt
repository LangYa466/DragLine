package me.nelly.module.misc

import nellyobfuscator.NellyClassObfuscator
import dragline.DragLine
import dragline.api.minecraft.client.entity.IEntityLivingBase
import dragline.event.AttackEvent
import dragline.event.EventTarget
import dragline.event.PacketEvent
import dragline.event.UpdateEvent
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.injection.backend.unwrap
import dragline.ui.client.hud.element.elements.Notification
import dragline.ui.client.hud.element.elements.NotifyType
import dragline.value.BoolValue
import dragline.value.ListValue
import dragline.value.TextValue
import net.minecraft.network.play.server.SPacketChat
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock
import java.util.regex.Pattern

@Native
@RegisterLock
@NellyClassObfuscator
@ModuleInfo(name = "AutoL", description = "NELLY", category = ModuleCategory.MISC)
class AutoL : Module() {
    private val killautoL = BoolValue("KillPlayerAutoL", true)
    private val killmusic = BoolValue("KillPlayerPlaySound",true)
    private val killautoLmode = ListValue("KillPlayerAutoL-Mode", arrayOf("SendMessage", "AddNotification"), "AddNotification").displayable {killautoL.get()}
    private val killutolmessage = TextValue("KillPlayerAutoL-Message", "我可是${DragLine.CLIENT_NAME}用户 |").displayable {killautoLmode.get().equals("SendMessage")}
    private val banAutoL = BoolValue("BanAutoL", true)
    private val banautoLmode = ListValue("BanAutoL-Mode", arrayOf("SendMessage", "AddNotification"), "AddNotification").displayable {banAutoL.get()}
    private val banautolmessage = TextValue("BanAutoL-Message", "我可是${DragLine.CLIENT_NAME}用户 |").displayable {banautoLmode.get().equals("SendMessage")}
    private val hubAutoL = BoolValue("HubAutoL", true)
    private val hubautoLmode = ListValue("HubAutoL-Mode", arrayOf("SendMessage", "AddNotification"), "AddNotification").displayable {banAutoL.get()}
    private val hubautolmessage = TextValue("HubAutoL-Message", "我可是${DragLine.CLIENT_NAME}用户 |").displayable {banautoLmode.get().equals("SendMessage")}

    var killCounts = 0
    var ban = 0
    var hub = 0
    var syncEntity: IEntityLivingBase? = null

    @EventTarget
    fun onAttack(event: AttackEvent) {
        syncEntity = event.targetEntity as IEntityLivingBase?
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(syncEntity!!.isDead && syncEntity != mc2.player){
            ++killCounts
            killautoL()
            syncEntity = null
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (packet is SPacketChat) {
            val matcher2 = Pattern.compile("离开了游戏").matcher(packet.chatComponent.unformattedText)
            if (matcher2.find()) {
                hub++
                val hubname = matcher2.group(1)
                if (hubAutoL.get() && hubautoLmode.get().contains("AddNotification")) {
                    DragLine.hud.addNotification(
                        Notification(
                            "HubChecker",
                            "$hubname is hub. Total hubs: $hub",
                            NotifyType.INFO
                        )
                    )
                }
                if (hubAutoL.get() && hubautoLmode.get().contains("SendMessage")) {
                    mc.thePlayer!!.sendChatMessage("@${hubautolmessage.get()} $hubname 已被封禁")
                }
            }
            val matcher =
                Pattern.compile("玩家(.*?)在本局游戏中行为异常").matcher(packet.chatComponent.unformattedText)
            if (matcher.find()) {
                ban++
                val banname = matcher.group(1)
                if (banAutoL.get() && banautoLmode.get().contains("AddNotification")) {
                    DragLine.hud.addNotification(
                        Notification(
                            "BanChecker",
                            "$$banname has been banned. Total bans: $ban",
                            NotifyType.INFO
                        )
                    )
                }
                if (banAutoL.get() && banautoLmode.get().contains("SendMessage")) {
                    mc.thePlayer!!.sendChatMessage("@${banautolmessage.get()} $banname 已被封禁")
                }
            }
        }
    }

    private fun killautoL() {
        if(killautoL.get() && killautoLmode.get().contains("SendMessage") ) {
            mc.thePlayer!!.sendChatMessage("@${killutolmessage.get()} ${syncEntity!!.displayName} 已被封禁")
        }

        if(killautoL.get() && killautoLmode.get().contains("AddNotification") ) {
            DragLine.hud.addNotification(
                Notification(
                    "Kills +1",
                    "Killed: $killCounts Players",
                    NotifyType.INFO
                )
            )

            if(killmusic.get() ) {
                DragLine.tipSoundManager.killSound.asyncPlay()
            }
        }
    }

    override val tag: String
        get() = killCounts.toString()
}



