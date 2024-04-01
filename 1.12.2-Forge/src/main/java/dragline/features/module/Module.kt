/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module

import dragline.DragLine
import dragline.event.Listenable
import net.ccbluex.liquidbounce.injection.backend.Backend
import dragline.ui.client.hud.element.elements.Notification
import dragline.ui.client.hud.element.elements.NotifyType
import dragline.utils.ClientUtils2
import dragline.utils.MinecraftInstance
import dragline.utils.render.ColorUtils.stripColor
import dragline.value.Value
import org.lwjgl.input.Keyboard

open class Module : MinecraftInstance(), Listenable {
    var isSupported: Boolean
    var expanded: Boolean = false

    // Module information
    // TODO: Remove ModuleInfo and change to constructor (#Kotlin)
    var name: String
    var description: String
    var category: ModuleCategory
    var keyBind = Keyboard.CHAR_NONE
        set(keyBind) {
            field = keyBind

            if (!DragLine.isStarting)
                DragLine.fileManager.saveConfig(DragLine.fileManager.modulesConfig)
        }
    var array = true
        set(array) {
            field = array

            if (!DragLine.isStarting)
                DragLine.fileManager.saveConfig(DragLine.fileManager.modulesConfig)
        }
    private val canEnable: Boolean

    var slideStep = 0F

    init {
        val moduleInfo = javaClass.getAnnotation(ModuleInfo::class.java)!!

        name = moduleInfo.name
        description = moduleInfo.description
        category = moduleInfo.category
        keyBind = moduleInfo.keyBind
        array = moduleInfo.array
        canEnable = moduleInfo.canEnable
        isSupported = Backend.REPRESENTED_BACKEND_VERSION in moduleInfo.supportedVersions
    }

    // Current state of module
    var state = false
        set(value) {
            if (field == value)
                return

            // Call toggle
            onToggle(value)

            // Play sound and add notification
            if (!DragLine.isStarting) {
                when (DragLine.moduleManager.toggleSoundMode) {
                    1 -> (if (value) mc.soundHandler.playSound(
                        "block.stone_pressureplate.click_on",
                        1F
                    ) else mc.soundHandler.playSound("block.stone_pressureplate.click_off", 1F))

                    2 -> (if (value) DragLine.tipSoundManager.enableSound else DragLine.tipSoundManager.disableSound).asyncPlay()
                    3 -> (if (value) DragLine.tipSoundManager.sigmaenableSound else DragLine.tipSoundManager.sigmadisableSound).asyncPlay()
                    4 -> (if (value) DragLine.tipSoundManager.sinkaenableSound else DragLine.tipSoundManager.sinkadisableSound).asyncPlay()
                    5 -> (if (value) DragLine.tipSoundManager.fallenenableSound else DragLine.tipSoundManager.fallendisableSound).asyncPlay()
                    6 -> (if (value) DragLine.tipSoundManager.prideenableSound else DragLine.tipSoundManager.pridedisableSound).asyncPlay()
                    7 -> (if (value) DragLine.tipSoundManager.lbplusenableSound else DragLine.tipSoundManager.lbplusdisableSound).asyncPlay()
                }
                when (DragLine.moduleManager.toggleMessageMode) {
                    1 -> when (DragLine.moduleManager.toggleChatMode) {
                        0 -> ClientUtils2.displayChatMessage(true,"${if (value) "§aEnabled" else "§cDisabled"} §r${name}.")
                        1 -> ClientUtils2.displayChatMessage(true,"§r${name} was ${if (value) "§aEnabled" else "§cDisabled"}.")
                    }
                    2 -> DragLine.hud.addNotification(
                        Notification(
                            "Module", "${
                                if (value) "Enabled "
                                else "Disabled "
                            }$name", if (value) NotifyType.SUCCESS
                            else NotifyType.ERROR
                        )
                    )
                }
            }


            // Call on enabled or disabled
            if (value) {
                onEnable()

                if (canEnable)
                    field = true
            } else {
                onDisable()
                field = false
            }

            // Save module state
            DragLine.fileManager.saveConfig(DragLine.fileManager.modulesConfig)
        }


    // HUD
    val hue = Math.random().toFloat()
    var slide = 0F
    var BreakName : Boolean = false
    var higt = 0F

    // Tag
    open val tag: String?
        get() = null

    val tagName: String
        get() = "$name${if (tag == null) "" else " §7$tag"}"

    val colorlessTagName: String
        get() = "$name${if (tag == null) "" else " " + stripColor(tag)}"

    fun breakname(toggle : Boolean) : String {
        var detName = name

        if(toggle) {
            when (detName.toLowerCase()) {
                "autotool" -> return "Auto Tool"
                "noslow" -> return "No Slow"
                "chestaura" -> return "Chest Aura"
                "cheststealer" -> return "Chest Stealer"
                "invcleaner" -> return "Inv Cleaner"
                "invmove" -> return "Inv Move"
                "autopot" -> return "Auto Potion"
                "blockfly" -> return "Block Fly"
                "fastbreak" -> return "Fast Break"
                "fastplace" -> return "Fast Place"
                "noswing" -> return "No Swing"
                "nametags" -> return "Name Tags"
                "itemesp" -> return "Item ESP"
                "freecam" -> return "Free Cam"
                "blockesp" -> return "Block ESP"
                "antiblind" -> return "Anti Blind"
                "blockoverlay" -> return "Block Overlay"
                "nofall" -> return "No Fall"
                "fastuse" -> return "Fast Use"
                "autorespawn" -> return "Auto Respawn"
                "autofish" -> return "Auto Fish"
                "antiafk" -> return "Anti AFK"
                "anticactus" -> return "Anti Cactus"
                "potionsaver" -> return "Potion Saver"
                "safewalk" -> return "Safe Walk"
                "noweb" -> return "No Web"
                "noclip" -> return "No Clip"
                "longjump" -> return "Long Jump"
                "liquidwalk" -> return "Liquid Walk"
                "highjump" -> return "High Jump"
                "icespeed" -> return "Ice Speed"
                "faststairs" -> return "Fast Stairs"
                "fastclimb" -> return "Fast Climb"
                "antifall" -> return "Anti Fall"
                "bufferspeed" -> return "Buffer Speed"
                "blockwalk" -> return "Block Walk"
                "autowalk" -> return "Auto Walk"
                "midclick" -> return "Mid Click"
                "liquidchat" -> return "Liquid Chat"
                "antibot" -> return "Anti Bot"
                "skinderp" -> return "Skin Derp"
                "pingspoof" -> return "Ping Spoof"
                "consolespammer" -> return "Console Spammer"
                "keepcontainer" -> return "Keep Container"
                "abortbreaking" -> return "Abort Breaking"
                "nofriends" -> return "No Friends"
                "hitbox" -> return "Hit Box"
                "fastbow" -> return "Fast Bow"
                "bowaimbot" -> return "Bow Aimbot"
                "autoweapon" -> return "Auto Weapon"
                "autosoup" -> return "Auto Soup"
                "autoclicker" -> return "Auto Clicker"
                "autobow" -> return "Auto Bow"
                "autoarmor" -> return "Auto Armor"
                "speedmine" -> return "Speed Mine"
                "targethud" -> return "Target HUD"
                "pointeresp" -> return "Pointer ESP"
                "playerface" -> return "Player Face"
                "itemrotate" -> return "Item Rotate"
                "targetstrafe" -> return "Target Strafe"
                "hytrun" -> return "HYT Run"
                "autojump" -> return "Auto Jump"
                "memoryfixer" -> return "Memory Fixer"
                "lagback" -> return "Lag Back"
                "autohead" -> return "Auto Head"
                "autosword" -> return "Auto Sword"
                "keepalive" -> return "Keep Alive"
                "killaura" -> return "Kill Aura"

            }
        }
        return detName
    }
    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    /**
     * Called when module toggled
     */
    open fun onToggle(state: Boolean) {}

    /**
     * Called when module enabled
     */
    open fun onEnable() {}

    /**
     * Called when module disabled
     */
    open fun onDisable() {}

    /**
     * Get module by [valueName]
     */
    open fun getValue(valueName: String) = values.find { it.name.equals(valueName, ignoreCase = true) }

    /**
     * Get all values of module
     */
    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Events should be handled when module is enabled
     */
    override fun handleEvents() = state
}