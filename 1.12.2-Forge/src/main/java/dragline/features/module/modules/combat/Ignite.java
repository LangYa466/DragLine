/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.combat;

import dragline.utils.MinecraftInstance;
import dragline.api.enums.EnumFacingType;
import dragline.api.enums.ItemType;
import dragline.api.minecraft.client.entity.IEntity;
import dragline.api.minecraft.client.entity.IEntityPlayerSP;
import dragline.api.minecraft.client.multiplayer.IWorldClient;
import dragline.api.minecraft.item.IItemStack;
import dragline.api.minecraft.util.IEnumFacing;
import dragline.api.minecraft.util.WBlockPos;
import dragline.api.minecraft.util.WMathHelper;
import dragline.api.minecraft.util.WVec3;
import dragline.event.EventTarget;
import dragline.event.UpdateEvent;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.utils.EntityUtils;
import dragline.utils.InventoryUtils;
import dragline.utils.RotationUtils;
import dragline.utils.block.BlockUtils;
import dragline.utils.timer.MSTimer;
import dragline.value.BoolValue;

@ModuleInfo(name = "Ignite", description = "Automatically sets targets around you on fire.", category = ModuleCategory.COMBAT)
public class Ignite extends Module {
   private final BoolValue lighterValue = new BoolValue("Lighter", true);
   private final BoolValue lavaBucketValue = new BoolValue("Lava", true);

   private final MSTimer msTimer = new MSTimer();

   @EventTarget
   public void onUpdate(final UpdateEvent event) {
       if (!msTimer.hasTimePassed(500L))
           return;

       IEntityPlayerSP thePlayer = MinecraftInstance.mc.getThePlayer();
       IWorldClient theWorld = MinecraftInstance.mc.getTheWorld();

       if (thePlayer == null || theWorld == null)
           return;

       final int lighterInHotbar =
               lighterValue.get() ? InventoryUtils.findItem(36, 45, MinecraftInstance.classProvider.getItemEnum(ItemType.FLINT_AND_STEEL)) : -1;
       final int lavaInHotbar =
               lavaBucketValue.get() ? InventoryUtils.findItem(26, 45, MinecraftInstance.classProvider.getItemEnum(ItemType.LAVA_BUCKET)) : -1;

       if (lighterInHotbar == -1 && lavaInHotbar == -1)
           return;

       final int fireInHotbar = lighterInHotbar != -1 ? lighterInHotbar : lavaInHotbar;

       for (final IEntity entity : theWorld.getLoadedEntityList()) {
           if (EntityUtils.isSelected(entity, true) && !entity.isBurning()) {
               WBlockPos blockPos = entity.getPosition();

               if (MinecraftInstance.mc.getThePlayer().getDistanceSq(blockPos) >= 22.3D ||
                       !BlockUtils.isReplaceable(blockPos) ||
                       !MinecraftInstance.classProvider.isBlockAir(BlockUtils.getBlock(blockPos)))
                   continue;

               RotationUtils.keepCurrentRotation = true;

               MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketHeldItemChange(fireInHotbar - 36));

               final IItemStack itemStack = MinecraftInstance.mc.getThePlayer().getInventory().getStackInSlot(fireInHotbar);

               if (MinecraftInstance.classProvider.isItemBucket(itemStack.getItem())) {
                   final double diffX = blockPos.getX() + 0.5D - MinecraftInstance.mc.getThePlayer().getPosX();
                   final double diffY = blockPos.getY() + 0.5D -
                           (thePlayer.getEntityBoundingBox().getMinY() +
                                   thePlayer.getEyeHeight());
                   final double diffZ = blockPos.getZ() + 0.5D - thePlayer.getPosZ();
                   final double sqrt = Math.sqrt(diffX * diffX + diffZ * diffZ);
                   final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90F;
                   final float pitch = (float) -(Math.atan2(diffY, sqrt) * 180.0D / Math.PI);

                   MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketPlayerLook(
                           thePlayer.getRotationYaw() +
                                   WMathHelper.wrapAngleTo180_float(yaw - thePlayer.getRotationYaw()),
                           thePlayer.getRotationPitch() +
                                   WMathHelper.wrapAngleTo180_float(pitch - thePlayer.getRotationPitch()),
                           thePlayer.getOnGround()));

                   MinecraftInstance.mc.getPlayerController().sendUseItem(thePlayer, theWorld, itemStack);
               } else {
                   for (EnumFacingType enumFacingType : EnumFacingType.values()) {
                       IEnumFacing side = MinecraftInstance.classProvider.getEnumFacing(enumFacingType);

                       final WBlockPos neighbor = blockPos.offset(side);

                       if (!BlockUtils.canBeClicked(neighbor)) continue;

                       final double diffX = neighbor.getX() + 0.5D - thePlayer.getPosX();
                       final double diffY = neighbor.getY() + 0.5D -
                               (thePlayer.getEntityBoundingBox().getMinY() +
                                       thePlayer.getEyeHeight());
                       final double diffZ = neighbor.getZ() + 0.5D - thePlayer.getPosZ();
                       final double sqrt = Math.sqrt(diffX * diffX + diffZ * diffZ);
                       final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90F;
                       final float pitch = (float) -(Math.atan2(diffY, sqrt) * 180.0D / Math.PI);

                       MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketPlayerLook(
                               thePlayer.getRotationYaw() +
                                       WMathHelper.wrapAngleTo180_float(yaw - thePlayer.getRotationYaw()),
                               thePlayer.getRotationPitch() +
                                       WMathHelper.wrapAngleTo180_float(pitch - thePlayer.getRotationPitch()),
                               thePlayer.getOnGround()));

                       if (MinecraftInstance.mc.getPlayerController().onPlayerRightClick(thePlayer, theWorld, itemStack, neighbor,
                               side.getOpposite(), new WVec3(side.getDirectionVec()))) {
                           thePlayer.swingItem();
                           break;
                       }
                   }
               }

               MinecraftInstance.mc.getNetHandler()
                       .addToSendQueue(MinecraftInstance.classProvider.createCPacketHeldItemChange(thePlayer.getInventory().getCurrentItem()));
               RotationUtils.keepCurrentRotation = false;
               MinecraftInstance.mc.getNetHandler().addToSendQueue(
                       MinecraftInstance.classProvider.createCPacketPlayerLook(thePlayer.getRotationYaw(), thePlayer.getRotationPitch(), thePlayer.getOnGround())
               );

               msTimer.reset();
               break;
           }
      }
   }
}
