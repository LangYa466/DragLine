/*
 * DragLine Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/DragLine/
 */

package dragline.script

import jdk.internal.dynalink.beans.StaticClass
import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import jdk.nashorn.api.scripting.ScriptUtils
import dragline.DragLine
import dragline.features.command.Command
import dragline.features.module.Module
import dragline.script.api.ScriptCommand
import dragline.script.api.ScriptModule
import dragline.script.api.ScriptTab
import dragline.script.api.global.Chat
import dragline.script.api.global.Item
import dragline.script.api.global.Setting
import dragline.utils.ClientUtils
import dragline.utils.MinecraftInstance
import net.ccbluex.liquidbounce.injection.backend.unwrap
import java.io.File
import java.util.function.Function
import javax.script.ScriptEngine

class Script(val scriptFile: File) : MinecraftInstance() {

    private val scriptEngine: ScriptEngine
    private val scriptText: String = scriptFile.readText()

    // Script information
    lateinit var scriptName: String
    lateinit var scriptVersion: String
    lateinit var scriptAuthors: Array<String>

    private var state = false

    private val events = HashMap<String, JSObject>()

    private val registeredModules = mutableListOf<Module>()
    private val registeredCommands = mutableListOf<Command>()

    init {
        val engineFlags = getMagicComment("engine_flags")?.split(",")?.toTypedArray() ?: emptyArray()
        scriptEngine = NashornScriptEngineFactory().getScriptEngine(*engineFlags)

        // Global classes
        scriptEngine.put("Chat", StaticClass.forClass(Chat::class.java))
        scriptEngine.put("Setting", StaticClass.forClass(Setting::class.java))
        scriptEngine.put("Item", StaticClass.forClass(Item::class.java))

        // Global instances
        scriptEngine.put("mc", mc.unwrap())
        scriptEngine.put("mc2", mc2)

        scriptEngine.put("moduleManager", DragLine.moduleManager)
        scriptEngine.put("eventManager", DragLine.eventManager)
        scriptEngine.put("commandManager", DragLine.commandManager)
        scriptEngine.put("scriptManager", DragLine.scriptManager)

        // Cross version instances
        scriptEngine.put("imc", mc)
        scriptEngine.put("classProvider", classProvider)

        // Global functions
        scriptEngine.put("registerScript", RegisterScript())

        supportLegacyScripts()
    }

    fun initScript() {
        scriptEngine.eval(scriptText)

        callEvent("load")

        ClientUtils.getLogger().info("[ScriptAPI] 成功加载脚本 '${scriptFile.name}'.")
    }

    @Suppress("UNCHECKED_CAST")
    inner class RegisterScript : Function<JSObject, Script> {
        /**
         * Global function 'registerScript' which is called to register a script.
         * @param scriptObject JavaScript object containing information about the script.
         * @return The instance of this script.
         */
        override fun apply(scriptObject: JSObject): Script {
            scriptName = scriptObject.getMember("name") as String
            scriptVersion = scriptObject.getMember("version") as String
            scriptAuthors =
                ScriptUtils.convert(scriptObject.getMember("authors"), Array<String>::class.java) as Array<String>

            return this@Script
        }
    }

    /**
     * Registers a new script module.
     * @param moduleObject JavaScript object containing information about the module.
     * @param callback JavaScript function to which the corresponding instance of [ScriptModule] is passed.
     * @see ScriptModule
     */
    @Suppress("unused")
    fun registerModule(moduleObject: JSObject, callback: JSObject) {
        val module = ScriptModule(moduleObject)
        DragLine.moduleManager.registerModule(module)
        registeredModules += module
        callback.call(moduleObject, module)
    }

    /**
     * Registers a new script command.
     * @param commandObject JavaScript object containing information about the command.
     * @param callback JavaScript function to which the corresponding instance of [ScriptCommand] is passed.
     * @see ScriptCommand
     */
    @Suppress("unused")
    fun registerCommand(commandObject: JSObject, callback: JSObject) {
        val command = ScriptCommand(commandObject)
        DragLine.commandManager.registerCommand(command)
        registeredCommands += command
        callback.call(commandObject, command)
    }

    /**
     * Registers a new creative inventory tab.
     * @param tabObject JavaScript object containing information about the tab.
     * @see ScriptTab
     */
    @Suppress("unused")
    fun registerTab(tabObject: JSObject) {
        ScriptTab(tabObject)
    }

    /**
     * Gets the value of a magic comment from the script. Used for specifying additional information about the script.
     * @param name Name of the comment.
     * @return Value of the comment.
     */
    private fun getMagicComment(name: String): String? {
        val magicPrefix = "///"

        scriptText.lines().forEach {
            if (!it.startsWith(magicPrefix)) return null

            val commentData = it.substring(magicPrefix.length).split("=", limit = 2)

            if (commentData.first().trim() == name) {
                return commentData.last().trim()
            }
        }

        return null
    }

    /**
     * Adds support for scripts made for LiquidBounce's original script API.
     */
    private fun supportLegacyScripts() {
        if (getMagicComment("api_version") != "2") {
            ClientUtils.getLogger().info("[ScriptAPI] 使用旧版支持运行脚本 '${scriptFile.name}'。")
            val legacyScript = DragLine::class.java.getResource("/assets/minecraft/DragLine/scriptapi/legacy.js")
                ?.readText()
            scriptEngine.eval(legacyScript)
        }
    }

    /**
     * Called from inside the script to register a new event handler.
     * @param eventName Name of the event.
     * @param handler JavaScript function used to handle the event.
     */
    fun on(eventName: String, handler: JSObject) {
        events[eventName] = handler
    }

    /**
     * Called when the client enables the script.
     */
    fun onEnable() {
        if (state) return

        callEvent("enable")
        state = true
    }

    /**
     * Called when the client disables the script. Handles unregistering all modules and commands
     * created with this script.
     */
    fun onDisable() {
        if (!state) return

        registeredModules.forEach { DragLine.moduleManager.unregisterModule(it) }
        registeredCommands.forEach { DragLine.commandManager.unregisterCommand(it) }

        callEvent("disable")
        state = false
    }

    /**
     * Imports another JavaScript file into the context of this script.
     * @param scriptFile Path to the file to be imported.
     */
    fun import(scriptFile: String) {
        val scriptText = File(DragLine.scriptManager.scriptsFolder, scriptFile).readText()

        scriptEngine.eval(scriptText)
    }

    /**
     * Calls the handler of a registered event.
     * @param eventName Name of the event to be called.
     */
    private fun callEvent(eventName: String) {
        try {
            events[eventName]?.call(null)
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("[ScriptAPI] Exception in script '$scriptName'!", throwable)
        }
    }
}