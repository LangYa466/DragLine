/*
 * DragLine Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/DragLine/
 */

package dragline.script

import dragline.DragLine
import dragline.utils.ClientUtils
import nellyobfuscator.NellyClassObfuscator
import java.io.File
import java.io.FileFilter

@NellyClassObfuscator
class ScriptManager {

    val scripts = mutableListOf<Script>()

    val scriptsFolder = File(DragLine.fileManager.dir, "scripts")
    private val scriptFileExtension = ".js"

    /**
     * Loads all scripts inside the scripts folder.
     */
    fun loadScripts() {
        if (!scriptsFolder.exists())
            scriptsFolder.mkdir()

        scriptsFolder.listFiles(FileFilter { it.name.endsWith(scriptFileExtension) })
            ?.forEach(this@ScriptManager::loadScript)
    }

    /**
     * Unloads all scripts.
     */
    fun unloadScripts() {
        scripts.clear()
    }

    /**
     * Loads a script from a file.
     */
    fun loadScript(scriptFile: File) {
        try {
            val script = Script(scriptFile)
            script.initScript()
            scripts.add(script)
        } catch (t: Throwable) {
            ClientUtils.getLogger().error("[ScriptAPI] 加载脚本 '${scriptFile.name}' 失败。", t)
        }
    }

    /**
     * Enables all scripts.
     */
    fun enableScripts() {
        scripts.forEach { it.onEnable() }
    }

    /**
     * Disables all scripts.
     */
    fun disableScripts() {
        scripts.forEach { it.onDisable() }
    }

    /**
     * Imports a script.
     * @param file JavaScript file to be imported.
     */
    fun importScript(file: File) {
        val scriptFile = File(scriptsFolder, file.name)
        file.copyTo(scriptFile)

        loadScript(scriptFile)
        ClientUtils.getLogger().info("[ScriptAPI] 导入脚本 '${scriptFile.name}' 成功。")
    }

    /**
     * Deletes a script.
     * @param script Script to be deleted.
     */
    fun deleteScript(script: Script) {
        script.onDisable()
        scripts.remove(script)
        script.scriptFile.delete()

        ClientUtils.getLogger().info("[ScriptAPI] 删除脚本 '${script.scriptFile.name}' 成功。")
    }

    /**
     * Reloads all scripts.
     */
    fun reloadScripts() {
        disableScripts()
        unloadScripts()
        loadScripts()
        enableScripts()

        ClientUtils.getLogger().info("[ScriptAPI] 重载脚本成功。")
    }
}