package dragline.features.module.modules.client

import dragline.DragLine
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.ui.cnfont.FontLoaders
import dragline.ui.font.Fonts
import dragline.value.FloatValue
import dragline.value.ListValue
import java.awt.TrayIcon

@ModuleInfo("CustomFonts", description = "CustomFonts",  category = ModuleCategory.CLIENT)
class CustomFonts : Module() {
    companion object {
        val enfontmode = ListValue(
            "Font-Mode",
            arrayOf("Tenacity", "SFUI","JelloLight","SFBold","Tahoma","JelloMedium","Roboto-Bold","Roboto-Medium"),
            "Tenacity"
        )
        val fontmode = ListValue(
            "CNFont-Mode",
            arrayOf("Genshin"),
            "Genshin"
        )

        val fontsize = FloatValue("CNFont-Size", 40F, 1F, 100F)

        fun getFont(): String {
            return fontmode.get()
        }

        fun getenFont(): String {
            return enfontmode.get().toLowerCase() + ".ttf"
        }

        fun getSize(): Float {
            return fontsize.get()
        }
    }

    override fun onEnable() {
        DragLine.showNotification("DragLine","开始重载字体系统",TrayIcon.MessageType.INFO)
        Fonts.loadFonts()

        FontLoaders.initFonts()
    }

}