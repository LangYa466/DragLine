/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.movement;

import dragline.utils.MinecraftInstance;
import dragline.api.minecraft.potion.PotionType;
import dragline.event.EventTarget;
import dragline.event.UpdateEvent;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.utils.MovementUtils;
import dragline.utils.Rotation;
import dragline.utils.RotationUtils;
import dragline.value.BoolValue;

@ModuleInfo(name = "Sprint", description = "Automatically sprints all the time.", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    public final BoolValue allDirectionsValue = new BoolValue("AllDirections", true);
    public final BoolValue blindnessValue = new BoolValue("Blindness", true);
    public final BoolValue foodValue = new BoolValue("Food", true);

    public final BoolValue checkServerSide = new BoolValue("CheckServerSide", false);
    public final BoolValue checkServerSideGround = new BoolValue("CheckServerSideOnlyGround", false);

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (!MovementUtils.isMoving() || MinecraftInstance.mc.getThePlayer().isSneaking() ||
                (blindnessValue.get() && MinecraftInstance.mc.getThePlayer().isPotionActive(MinecraftInstance.classProvider.getPotionEnum(PotionType.BLINDNESS))) ||
                (foodValue.get() && !(MinecraftInstance.mc.getThePlayer().getFoodStats().getFoodLevel() > 6.0F || MinecraftInstance.mc.getThePlayer().getCapabilities().getAllowFlying()))
                || (checkServerSide.get() && (MinecraftInstance.mc.getThePlayer().getOnGround() || !checkServerSideGround.get())
                && !allDirectionsValue.get() && RotationUtils.targetRotation != null &&
                RotationUtils.getRotationDifference(new Rotation(MinecraftInstance.mc.getThePlayer().getRotationYaw(), MinecraftInstance.mc.getThePlayer().getRotationPitch())) > 30)) {
            MinecraftInstance.mc.getThePlayer().setSprinting(false);
            return;
        }

        if (allDirectionsValue.get() || MinecraftInstance.mc.getThePlayer().getMovementInput().getMoveForward() >= 0.8F)
            MinecraftInstance.mc.getThePlayer().setSprinting(true);
    }
}
