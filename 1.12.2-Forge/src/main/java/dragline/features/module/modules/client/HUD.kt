/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.client

import dragline.DragLine
import dragline.event.*
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.value.BoolValue
import dragline.value.FloatValue
import dragline.value.IntegerValue
import dragline.value.ListValue

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.CLIENT, array = false)
class HUD : Module() {
    private val toggleMessageModeValue =
        ListValue("ToggleMessageMode", arrayOf("None", "Chat", "Notifications"), "Notifications")
    private val chatNotificationsModeValue = ListValue("ChatStyle", arrayOf("Enabled-Sth","Sth-Enabled"),"Enabled-Sth").displayable { toggleMessageModeValue.get().equals("Chat") }
    private val toggleSoundValue = ListValue(
        "ToggleSound",
        arrayOf("None", "Default", "Custom", "Sigma", "Sinka", "Fallen", "Pride", "LB+"),
        "Custom"
    )
    val shadowValue = ListValue("TextShadowMode", arrayOf("LiquidBounce", "Outline", "Default", "Autumn"), "Autumn")
    val inventoryParticle = BoolValue("InventoryParticle", false)
    private val blurValue = BoolValue("Blur", false)
    val fontChatValue = BoolValue("FontChat", true)
    val ChatAnimValue = BoolValue("ChatAnim", false)
    val ChatRect = BoolValue("ChatRect", false)
    val Radius = IntegerValue("BlurRadius", 10 , 1 , 50 )

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (classProvider.isGuiHudDesigner(mc.currentScreen))
            return

        DragLine.hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        DragLine.hud.update()
    }
    @EventTarget(ignoreCondition = true)
    fun onTick(event: TickEvent) {
        DragLine.moduleManager.toggleMessageMode = toggleMessageModeValue.values.indexOf(toggleMessageModeValue.get())
        DragLine.moduleManager.toggleSoundMode = toggleSoundValue.values.indexOf(toggleSoundValue.get())
        DragLine.moduleManager.toggleChatMode = toggleSoundValue.values.indexOf(chatNotificationsModeValue.get())
    }
    @EventTarget
    fun onKey(event: KeyEvent) {
        DragLine.hud.handleKey('a', event.key)
    }

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.guiScreen != null &&
                !(classProvider.isGuiChat(event.guiScreen) || classProvider.isGuiHudDesigner(event.guiScreen))) mc.entityRenderer.loadShader(classProvider.createResourceLocation("liquidbounce" + "/blur.json")) else if (mc.entityRenderer.shaderGroup != null &&
                mc.entityRenderer.shaderGroup!!.shaderGroupName.contains("liquidbounce/blur.json")) mc.entityRenderer.stopUseShader()
    }

    companion object {
        @JvmField
        val gidentspeed = IntegerValue("GidentSpeed", 100, 1, 1000)
        @JvmField
        val redValue = IntegerValue("Red", 255, 0, 255)
        @JvmField
        val greenValue = IntegerValue("Green", 255, 0, 255)
        @JvmField
        val blueValue = IntegerValue("Blue", 255, 0, 255)
        @JvmField
        val redValue2 = IntegerValue("Red2", 255, 0, 255)
        @JvmField
        val greenValue2 = IntegerValue("Green2", 255, 0, 255)
        @JvmField
        val blueValue2 = IntegerValue("Blue2", 255, 0, 255)
        @JvmField
        val a = IntegerValue("A", 100, 0, 255)
        @JvmField
        val a2 = IntegerValue("A2", 100, 0, 255)
        @JvmField
        val ra = FloatValue("Radius", 6.44f, 0.1f, 10.0f)
        @JvmField
        val line = FloatValue("Line", 2f, 0f, 5f)
        @JvmField
        val office = FloatValue("Office", 3f, 0f, 5f)
        @JvmField
        val rainbowSpeed = IntegerValue("Rainbow-Speed", 1500, 500, 7000)
        @JvmField
        val blurRadius = IntegerValue("BlurRadius", 10, 1, 50)
        @JvmField
        val shadowValue = ListValue("ShadowMode", arrayOf("LiquidBounce", "Outline", "Default", "Custom"), "Outline")
        @JvmField
        val gradientSpeed = IntegerValue("GradientSpeed", 100, 10, 1000)
    }

    init {
        state = true
    }
}