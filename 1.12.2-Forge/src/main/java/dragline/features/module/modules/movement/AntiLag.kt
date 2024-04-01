package dragline.features.module.modules.movement

import dragline.DragLine.moduleManager
import dragline.event.EventTarget
import dragline.features.module.Module
import dragline.features.module.ModuleCategory
import dragline.features.module.ModuleInfo
import dragline.features.module.modules.combat.KillAura
import dragline.value.ListValue


@ModuleInfo(name = "AntiLag", description = "Recall", category = ModuleCategory.MOVEMENT)
class AntiLag : Module() {

    private val modeValue = ListValue("Mode", arrayOf("HYT","AAC5","Test"), "AntiCheat")
    private var ticks = 0
    private var a = 0
    private var b=0
    override fun onEnable() {
        ticks = 0
    }
    @EventTarget
    fun onUpdate(){
        when(modeValue.get().toLowerCase()){
            "aac5"->{

                val Killaura = moduleManager.getModule(KillAura::class.java) as KillAura
                if(mc.thePlayer!!.onGround){
                    if(b == 0){
                        Killaura.keepSprintValue.set(true)
                        b++
                    }
                }else{
                    b = 0
                    if(a == 0){
                        Killaura.keepSprintValue.set(false)
                        a++
                    }
                }
                if(ticks > 250){
                    if(mc.thePlayer!!.isOnLadder&&mc.gameSettings.keyBindJump.pressed){
                        mc.thePlayer!!.motionY=0.11
                    }
                }
                if(ticks > 500){
                    ticks = 0
                }else{
                    ticks++
                }
            }
            "hyt"->{
                if(ticks > 1000){
                    if(mc.thePlayer!!.isOnLadder&&mc.gameSettings.keyBindJump.pressed){
                        mc.thePlayer!!.motionY=0.11
                    }
                }
                if(ticks > 2000){
                    ticks = 0
                }else{
                    ticks++
                }
            }
            "test"->{
                if(ticks > 1000){
                    if(mc.thePlayer!!.isOnLadder&&mc.gameSettings.keyBindJump.pressed){
                        mc.thePlayer!!.motionY = 0.09
                        mc.thePlayer!!.motionY = 0.10
                        mc.thePlayer!!.motionY = 0.11
                    }
                }
                if(ticks > 2000){
                    ticks = 0
                }else{
                    ticks++
                }
            }
        }
    }
    override val tag: String
        get() = this.modeValue.get()
}
