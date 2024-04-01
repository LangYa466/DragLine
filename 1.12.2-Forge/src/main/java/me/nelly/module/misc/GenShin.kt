package me.nelly.module.misc

import nellyobfuscator.NellyClassObfuscator
import dragline.DragLine
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.utils.ClientUtils
import java.awt.TrayIcon
import java.io.File

@NellyClassObfuscator
@ModuleInfo(name = "GenShin", description = "Nelly", category = ModuleCategory.MISC)
class GenShin : Module() {

    override fun onEnable() {
        startgenshin()
        DragLine.showNotification("DragLine", "原神,启动!", TrayIcon.MessageType.INFO)
    }

    fun startgenshin() {
        val genshinpath = "C:\\Program Files\\Genshin Impact\\Genshin Impact Game\\YuanShen.exe"

        try {
            Runtime.getRuntime().exec(genshinpath)
            ClientUtils.getLogger().info("原神,启动!!!！")
            DragLine.tipSoundManager.genshinstartSound.asyncPlay()
            return
        } catch (e: Exception) {
            ClientUtils.getLogger().error("原神启动失败：${e.message}")
            return
        }

        ClientUtils.getLogger().error("没原神开你妈呢！")
    }
}