/*
 * LiquidBounce Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/LiquidBounce/
 */
package dragline.sound

import dragline.DragLine
import dragline.utils.FileUtils
import nellyobfuscator.NellyClassObfuscator
import java.io.File

@NellyClassObfuscator
class TipSoundManager {
    var killSound: TipSoundPlayer
    var genshinstartSound: TipSoundPlayer
    var enableSound: TipSoundPlayer
    var sigmaenableSound: TipSoundPlayer
    var lbplusenableSound: TipSoundPlayer
    var prideenableSound: TipSoundPlayer
    var sinkaenableSound: TipSoundPlayer
    var fallenenableSound: TipSoundPlayer

    var disableSound: TipSoundPlayer
    var sigmadisableSound: TipSoundPlayer
    var lbplusdisableSound: TipSoundPlayer
    var pridedisableSound: TipSoundPlayer
    var sinkadisableSound: TipSoundPlayer
    var fallendisableSound: TipSoundPlayer

    init {
        val enableSoundFile = File(DragLine.fileManager.soundsDir, "enable.wav")
        val disableSoundFile = File(DragLine.fileManager.soundsDir, "disable.wav")
        val sigmaenableSoundFile = File(DragLine.fileManager.soundsDir, "sigma_enable.wav")
        val sigmadisableSoundFile = File(DragLine.fileManager.soundsDir, "sigma_disable.wav")
        val lbplusenableSoundFile = File(DragLine.fileManager.soundsDir, "lbplus_enable.wav")
        val lbplusdisableSoundFile = File(DragLine.fileManager.soundsDir, "lbplus_disable.wav")
        val prideenableSoundFile = File(DragLine.fileManager.soundsDir, "pride_enable.wav")
        val pridedisableSoundFile = File(DragLine.fileManager.soundsDir, "pride_disable.wav")
        val sinkaenableSoundFile = File(DragLine.fileManager.soundsDir, "sinka_enable.wav")
        val sinkadisableSoundFile = File(DragLine.fileManager.soundsDir, "sinka_disable.wav")
        val fallenenableSoundFile = File(DragLine.fileManager.soundsDir, "fallen_enable.wav")
        val fallendisableSoundFile = File(DragLine.fileManager.soundsDir, "fallen_disable.wav")
        val genshinstartSoundFile = File(DragLine.fileManager.soundsDir, "genshin_start.wav")
        val killSoundFile = File(DragLine.fileManager.soundsDir, "kill.wav")

        if (!killSoundFile.exists())
            FileUtils.unpackFile(killSoundFile, "assets/minecraft/liquidbounce/sound/kill.wav")
        if (!genshinstartSoundFile.exists())
            FileUtils.unpackFile(genshinstartSoundFile, "assets/minecraft/liquidbounce/sound/genshin_start.wav")
        if (!enableSoundFile.exists())
            FileUtils.unpackFile(enableSoundFile, "assets/minecraft/liquidbounce/sound/enable.wav")
        if (!disableSoundFile.exists())
            FileUtils.unpackFile(disableSoundFile, "assets/minecraft/liquidbounce/sound/disable.wav")
        if (!sigmaenableSoundFile.exists())
            FileUtils.unpackFile(sigmaenableSoundFile, "assets/minecraft/liquidbounce/sound/sigma_enable.wav")
        if (!sigmadisableSoundFile.exists())
            FileUtils.unpackFile(sigmadisableSoundFile, "assets/minecraft/liquidbounce/sound/sigma_disable.wav")
        if (!fallenenableSoundFile.exists())
            FileUtils.unpackFile(fallenenableSoundFile, "assets/minecraft/liquidbounce/sound/fallen_enable.wav")
        if (!fallendisableSoundFile.exists())
            FileUtils.unpackFile(fallendisableSoundFile, "assets/minecraft/liquidbounce/sound/fallen_disable.wav")
        if (!prideenableSoundFile.exists())
            FileUtils.unpackFile(prideenableSoundFile, "assets/minecraft/liquidbounce/sound/pride_enable.wav")
        if (!pridedisableSoundFile.exists())
            FileUtils.unpackFile(pridedisableSoundFile, "assets/minecraft/liquidbounce/sound/pride_disable.wav")
        if (!lbplusenableSoundFile.exists())
            FileUtils.unpackFile(lbplusenableSoundFile, "assets/minecraft/liquidbounce/sound/lbplus_enable.wav")
        if (!lbplusdisableSoundFile.exists())
            FileUtils.unpackFile(lbplusdisableSoundFile, "assets/minecraft/liquidbounce/sound/lbplus_disable.wav")
        if (!sinkaenableSoundFile.exists())
            FileUtils.unpackFile(sinkaenableSoundFile, "assets/minecraft/liquidbounce/sound/sinka_enable.wav")
        if (!sinkadisableSoundFile.exists())
            FileUtils.unpackFile(sinkadisableSoundFile, "assets/minecraft/liquidbounce/sound/sigma_disable.wav")

        killSound = TipSoundPlayer(killSoundFile)
        genshinstartSound = TipSoundPlayer(genshinstartSoundFile)
        enableSound = TipSoundPlayer(enableSoundFile)
        disableSound = TipSoundPlayer(disableSoundFile)
        sigmaenableSound = TipSoundPlayer(sigmaenableSoundFile)
        sigmadisableSound = TipSoundPlayer(sigmadisableSoundFile)
        lbplusenableSound = TipSoundPlayer(lbplusenableSoundFile)
        lbplusdisableSound = TipSoundPlayer(lbplusdisableSoundFile)
        prideenableSound = TipSoundPlayer(prideenableSoundFile)
        pridedisableSound = TipSoundPlayer(pridedisableSoundFile)
        sinkaenableSound = TipSoundPlayer(sinkaenableSoundFile)
        sinkadisableSound = TipSoundPlayer(sinkadisableSoundFile)
        fallenenableSound = TipSoundPlayer(fallenenableSoundFile)
        fallendisableSound = TipSoundPlayer(fallendisableSoundFile)
    }
}