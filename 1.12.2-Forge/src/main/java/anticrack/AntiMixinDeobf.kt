package anticrack

import dragline.DragLine
import nellyobfuscator.NellyClassObfuscator
import dragline.event.EventManager
import dragline.features.command.CommandManager
import dragline.features.special.AntiForge
import dragline.features.special.BungeeCordSpoof
import dragline.features.special.DonatorCape
import dragline.file.FileManager
import dragline.utils.InventoryUtils
import dragline.utils.RotationUtils
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock

@Native
@RegisterLock
@NellyClassObfuscator
object AntiMixinDeobf {
    fun startGame() {
        // Create file manager
        DragLine.fileManager = FileManager()

        // Crate event manager
        DragLine.eventManager = EventManager()

        // Register listeners
        DragLine.eventManager.registerListener(RotationUtils())
        DragLine.eventManager.registerListener(AntiForge())
        DragLine.eventManager.registerListener(BungeeCordSpoof())
        DragLine.eventManager.registerListener(DonatorCape())
        DragLine.eventManager.registerListener(InventoryUtils())

        // Create command manager
        DragLine.commandManager = CommandManager()

        DragLine.startClient()

    }
}