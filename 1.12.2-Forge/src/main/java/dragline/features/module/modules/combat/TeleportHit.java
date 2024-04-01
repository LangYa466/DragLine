/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.combat;

import dragline.utils.*;
import dragline.api.minecraft.client.entity.IEntity;
import dragline.api.minecraft.client.entity.IEntityLivingBase;
import dragline.api.minecraft.client.entity.IEntityPlayerSP;
import dragline.api.minecraft.network.play.client.ICPacketUseEntity;
import dragline.api.minecraft.util.WVec3;
import dragline.event.EventState;
import dragline.event.EventTarget;
import dragline.event.MotionEvent;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.utils.*;

@ModuleInfo(name = "TeleportHit", description = "Allows to hit entities from far away.", category = ModuleCategory.COMBAT)
public class TeleportHit extends Module {
    private IEntityLivingBase targetEntity;
    private boolean shouldHit;

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (event.getEventState() != EventState.PRE)
            return;

        final IEntity facedEntity = RaycastUtils.raycastEntity(100D, MinecraftInstance.classProvider::isEntityLivingBase);

        IEntityPlayerSP thePlayer = MinecraftInstance.mc.getThePlayer();

        if (thePlayer == null)
            return;

        if (MinecraftInstance.mc.getGameSettings().getKeyBindAttack().isKeyDown() && EntityUtils.isSelected(facedEntity, true) && facedEntity.getDistanceSqToEntity(thePlayer) >= 1D)
            targetEntity = facedEntity.asEntityLivingBase();

        if (targetEntity != null) {
            if (!shouldHit) {
                shouldHit = true;
                return;
            }

            if (thePlayer.getFallDistance() > 0F) {
                final WVec3 rotationVector = RotationUtils.getVectorForRotation(new Rotation(thePlayer.getRotationYaw(), 0F));
                final double x = thePlayer.getPosX() + rotationVector.getXCoord() * (thePlayer.getDistanceToEntity(targetEntity) - 1.0F);
                final double z = thePlayer.getPosZ() + rotationVector.getZCoord() * (thePlayer.getDistanceToEntity(targetEntity) - 1.0F);
                final double y = targetEntity.getPosition().getY() + 0.25D;

                PathUtils.findPath(x, y + 1.0D, z, 4D).forEach(pos -> MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketPlayerPosition(pos.getX(), pos.getY(), pos.getZ(), false)));

                thePlayer.swingItem();
                MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketUseEntity(targetEntity, ICPacketUseEntity.WAction.ATTACK));
                thePlayer.onCriticalHit(targetEntity);
                shouldHit = false;
                targetEntity = null;
            } else if (thePlayer.getOnGround())
                thePlayer.jump();
        } else
            shouldHit = false;
    }
}
