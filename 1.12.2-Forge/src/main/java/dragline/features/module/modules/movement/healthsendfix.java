package dragline.features.module.modules.movement;

import dragline.utils.*;
import dragline.api.minecraft.potion.PotionType;
import dragline.event.EventTarget;
import dragline.event.UpdateEvent;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.value.BoolValue;
import scala.Int;

@ModuleInfo(name="HealthSendFix",description = "SendurMother",category = ModuleCategory.MOVEMENT)
public class healthsendfix extends Module{
    public final float MaxHealth = mc.getThePlayer().getMaxHealth();
    public final float NowHealth = mc.getThePlayer().getHealth();
    public int check1 = 0;
    public int check2 =0 ;
    public float changehealth=0;
    @Override
    public void onEnable() {
        ClientUtils.displayChatMessage("HP检测现在已经开启");
    }


    @Override
    public void onDisable() {
        ClientUtils.displayChatMessage("靠嫩娘已经关闭了");
        check1 = 0;
        check2 = 0;
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if(NowHealth==MaxHealth&&check1==0){
            ClientUtils.displayChatMessage("你现在是满血,我靠你奶");
            check1=1;
        }else{
            check1=0;
            if(check2==0){
                changehealth = MaxHealth - NowHealth;
                ClientUtils.displayChatMessage("[HPCheck]Your HP: "+NowHealth+" | HP变更:"+changehealth);
                check2=1;
            }
            if(changehealth!=MaxHealth-NowHealth){
                check2=0;
            }
        }
    }
}