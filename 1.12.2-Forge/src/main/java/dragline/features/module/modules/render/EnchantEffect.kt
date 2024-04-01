package dragline.features.module.modules.render

import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.BoolValue
import dragline.value.IntegerValue

@ModuleInfo(name = "EnchantEffect", description = "Change Sword Color", category = ModuleCategory.RENDER)
class EnchantEffect : Module() {
    private val colorRedValue = IntegerValue("R", 0, 0, 255)
    private val colorGreenValue = IntegerValue("G", 160, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 255, 0, 255)
    private val rainbow = BoolValue("RainBow", false)

    fun getRedValue(): IntegerValue? {
        return colorRedValue
    }

    fun getRainbow(): BoolValue? {
        return rainbow
    }

    fun getGreenValue(): IntegerValue? {
        return colorGreenValue
    }

    fun getBlueValue(): IntegerValue? {
        return colorBlueValue
    }

    fun getalphaValue(): IntegerValue? {
        return alphaValue
    }
}