/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.features.module.modules.world;

import dragline.api.minecraft.util.*;
import dragline.event.*;
import dragline.utils.*;
import dragline.DragLine;
import dragline.api.enums.BlockType;
import dragline.api.enums.EnumFacingType;
import dragline.api.minecraft.client.block.IBlock;
import dragline.api.minecraft.item.IItem;
import dragline.api.minecraft.item.IItemBlock;
import dragline.api.minecraft.item.IItemStack;
import dragline.api.minecraft.network.IPacket;
import dragline.api.minecraft.network.play.client.ICPacketEntityAction;
import dragline.api.minecraft.network.play.client.ICPacketHeldItemChange;
import dragline.api.minecraft.util.*;
import dragline.event.*;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.features.module.modules.render.BlockOverlay;
import dragline.ui.font.Fonts;
import dragline.utils.*;
import dragline.utils.block.BlockUtils;
import dragline.utils.block.PlaceInfo;
import dragline.utils.render.RenderUtils;
import dragline.utils.timer.MSTimer;
import dragline.utils.timer.TimeUtils;
import dragline.value.BoolValue;
import dragline.value.FloatValue;
import dragline.value.IntegerValue;
import dragline.value.ListValue;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "Scaffold", description = "Automatically places blocks beneath your feet.", category = ModuleCategory.WORLD, keyBind = Keyboard.KEY_I)
public class Scaffold extends Module {

    /**
     * OPTIONS
     */

    // Mode
    public final ListValue modeValue = new ListValue("Mode", new String[]{"Normal", "Rewinside", "Expand"}, "Normal");

    // Delay
    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 0, 0, 1000) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int i = minDelayValue.get();

            if (i > newValue)
                set(i);
        }
    };

    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 0, 0, 1000) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int i = maxDelayValue.get();

            if (i < newValue)
                set(i);
        }
    };
    private final BoolValue placeableDelay = new BoolValue("PlaceableDelay", false);

    // AutoBlock
    private final ListValue autoBlockValue = new ListValue("AutoBlock", new String[]{"Off", "Spoof", "Switch"}, "Spoof");

    // Basic stuff
    public final BoolValue sprintValue = new BoolValue("Sprint", true);
    private final BoolValue swingValue = new BoolValue("Swing", true);
    private final BoolValue searchValue = new BoolValue("Search", true);
    private final BoolValue downValue = new BoolValue("Down", true);
    private final BoolValue picker = new BoolValue("Picker", false);
    private final ListValue placeModeValue = new ListValue("PlaceTiming", new String[]{"Pre", "Post"}, "Post");

    // Eagle
    private final ListValue eagleValue = new ListValue("Eagle", new String[]{"Normal", "EdgeDistance", "Silent", "Off"}, "Off");
    private final IntegerValue blocksToEagleValue = new IntegerValue("BlocksToEagle", 0, 0, 10);
    private final FloatValue edgeDistanceValue = new FloatValue("EagleEdgeDistance", 0.2F, 0F, 0.5F);

    // Expand
    private final IntegerValue expandLengthValue = new IntegerValue("ExpandLength", 5, 1, 6);

    // RotationStrafe
    private final BoolValue rotationStrafeValue = new BoolValue("RotationStrafe", false);

    // Rotations
    private final ListValue rotationModeValue = new ListValue("RotationMode", new String[]{"Normal", "Static", "StaticPitch", "StaticYaw", "Off"}, "Normal");
    private final BoolValue silentRotation = new BoolValue("SilentRotation", true);
    private final BoolValue keepRotationValue = new BoolValue("KeepRotation", false);
    private final IntegerValue keepLengthValue = new IntegerValue("KeepRotationLength", 0, 0, 20);
    private final FloatValue staticPitchValue = new FloatValue("StaticPitchOffset", 86F, 70F, 90F);
    private final FloatValue staticYawOffsetValue = new FloatValue("StaticYawOffset", 0F, 0F, 90F);


    // Other
    private final FloatValue xzRangeValue = new FloatValue("xzRange", 0.8F, 0.1F, 1.0F);
    private final FloatValue yRangeValue = new FloatValue("yRange", 0.8F, 0.1F, 1.0F);

    // SearchAccuracy
    private final IntegerValue searchAccuracyValue = new IntegerValue("SearchAccuracy", 8, 1, 24) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            if (getMaximum() < newValue) {
                set(getMaximum());
            } else if (getMinimum() > newValue) {
                set(getMinimum());
            }
        }
    };

    // Turn Speed
    private final FloatValue maxTurnSpeedValue = new FloatValue("MaxTurnSpeed", 180, 1, 180) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            float v = minTurnSpeedValue.get();
            if (v > newValue) set(v);
            if (getMaximum() < newValue) {
                set(getMaximum());
            } else if (getMinimum() > newValue) {
                set(getMinimum());
            }
        }
    };
    private final FloatValue minTurnSpeedValue = new FloatValue("MinTurnSpeed", 180, 1, 180) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            float v = maxTurnSpeedValue.get();
            if (v < newValue) set(v);
            if (getMaximum() < newValue) {
                set(getMaximum());
            } else if (getMinimum() > newValue) {
                set(getMinimum());
            }
        }
    };

    // Zitter
    private final BoolValue zitterValue = new BoolValue("Zitter", false);
    private final ListValue zitterModeValue = new ListValue("ZitterMode", new String[]{"Teleport", "Smooth"}, "Teleport");
    private final FloatValue zitterSpeed = new FloatValue("ZitterSpeed", 0.13F, 0.1F, 0.3F);
    private final FloatValue zitterStrength = new FloatValue("ZitterStrength", 0.072F, 0.05F, 0.2F);

    // Game
    private final FloatValue timerValue = new FloatValue("Timer", 1F, 0.1F, 10F);
    private final FloatValue speedModifierValue = new FloatValue("SpeedModifier", 1F, 0, 2F);
    private final BoolValue slowValue = new BoolValue("Slow", false) {
        @Override
        protected void onChanged(final Boolean oldValue, final Boolean newValue) {
            if (newValue)
                sprintValue.set(false);
        }
    };
    private final FloatValue slowSpeed = new FloatValue("SlowSpeed", 0.6F, 0.2F, 0.8F);

    // Safety
    private final BoolValue sameYValue = new BoolValue("SameY", false);
    private final BoolValue safeWalkValue = new BoolValue("SafeWalk", true);
    private final BoolValue airSafeValue = new BoolValue("AirSafe", false);

    // Visuals
    private final BoolValue counterDisplayValue = new BoolValue("Counter", true);
    private final BoolValue markValue = new BoolValue("Mark", false);

    /**
     * MODULE
     */

    // Target block
    private PlaceInfo targetPlace;

    // Launch position
    private int launchY;

    // Rotation lock
    private Rotation lockRotation;
    private Rotation limitedRotation;
    private boolean facesBlock = false;

    // Auto block slot
    private int slot;

    // Zitter Smooth
    private boolean zitterDirection;

    // Delay
    private final MSTimer delayTimer = new MSTimer();
    private final MSTimer zitterTimer = new MSTimer();
    private long delay;

    // Eagle
    private int placedBlocksWithoutEagle = 0;
    private boolean eagleSneaking;

    // Down
    private boolean shouldGoDown = false;

    /**
     * Enable module
     */
    @Override
    public void onEnable() {
        if (MinecraftInstance.mc.getThePlayer() == null) return;

        launchY = (int) MinecraftInstance.mc.getThePlayer().getPosY();
    }

    /**
     * Update event
     *
     * @param event
     */
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        getBestBlocks();
        MinecraftInstance.mc.getTimer().setTimerSpeed(timerValue.get());


        shouldGoDown = downValue.get() && MinecraftInstance.mc.getGameSettings().isKeyDown(MinecraftInstance.mc.getGameSettings().getKeyBindSneak()) && getBlocksAmount() > 1;
        if (shouldGoDown)
            MinecraftInstance.mc.getGameSettings().getKeyBindSneak().setPressed(false);

        if (slowValue.get()) {
            MinecraftInstance.mc.getThePlayer().setMotionX(MinecraftInstance.mc.getThePlayer().getMotionX() * slowSpeed.get());
            MinecraftInstance.mc.getThePlayer().setMotionZ(MinecraftInstance.mc.getThePlayer().getMotionZ() * slowSpeed.get());
        }

        if (sprintValue.get()) {
            if (!MinecraftInstance.mc.getGameSettings().isKeyDown(MinecraftInstance.mc.getGameSettings().getKeyBindSprint())) {
                MinecraftInstance.mc.getGameSettings().getKeyBindSprint().setPressed(false);
            }
            if (MinecraftInstance.mc.getGameSettings().isKeyDown(MinecraftInstance.mc.getGameSettings().getKeyBindSprint())) {
                MinecraftInstance.mc.getGameSettings().getKeyBindSprint().setPressed(true);
            }
            if (MinecraftInstance.mc.getGameSettings().getKeyBindSprint().isKeyDown()) {
                MinecraftInstance.mc.getThePlayer().setSprinting(true);
            }
            if (!MinecraftInstance.mc.getGameSettings().getKeyBindSprint().isKeyDown()) {
                MinecraftInstance.mc.getThePlayer().setSprinting(false);
            }
        }

        if (MinecraftInstance.mc.getThePlayer().getOnGround()) {
            final String mode = modeValue.get();

            // Rewinside scaffold mode
            if (mode.equalsIgnoreCase("Rewinside")) {
                MovementUtils.strafe(0.2F);
                MinecraftInstance.mc.getThePlayer().setMotionY(0D);
            }

            // Smooth Zitter
            if (zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("smooth")) {
                if (!MinecraftInstance.mc.getGameSettings().isKeyDown(MinecraftInstance.mc.getGameSettings().getKeyBindRight()))
                    MinecraftInstance.mc.getGameSettings().getKeyBindRight().setPressed(false);

                if (!MinecraftInstance.mc.getGameSettings().isKeyDown(MinecraftInstance.mc.getGameSettings().getKeyBindLeft()))
                    MinecraftInstance.mc.getGameSettings().getKeyBindLeft().setPressed(false);

                if (zitterTimer.hasTimePassed(100)) {
                    zitterDirection = !zitterDirection;
                    zitterTimer.reset();
                }

                if (zitterDirection) {
                    MinecraftInstance.mc.getGameSettings().getKeyBindRight().setPressed(true);
                    MinecraftInstance.mc.getGameSettings().getKeyBindLeft().setPressed(false);
                } else {
                    MinecraftInstance.mc.getGameSettings().getKeyBindRight().setPressed(false);
                    MinecraftInstance.mc.getGameSettings().getKeyBindLeft().setPressed(true);
                }
            }

            // Eagle
            if (!eagleValue.get().equalsIgnoreCase("Off") && !shouldGoDown) {
                double dif = 0.5D;
                if (eagleValue.get().equalsIgnoreCase("EdgeDistance") && !shouldGoDown) {
                    for (int i = 0; i < 4; i++) {
                        switch (i) {
                            case 0: {
                                final WBlockPos blockPos = new WBlockPos(MinecraftInstance.mc.getThePlayer().getPosX() - 1, MinecraftInstance.mc.getThePlayer().getPosY() - (MinecraftInstance.mc.getThePlayer().getPosY() == (int) MinecraftInstance.mc.getThePlayer().getPosY() + 0.5D ? 0D : 1.0D), MinecraftInstance.mc.getThePlayer().getPosZ());
                                final PlaceInfo placeInfo = PlaceInfo.get(blockPos);

                                if (BlockUtils.isReplaceable(blockPos) && placeInfo != null) {
                                    double calcDif = MinecraftInstance.mc.getThePlayer().getPosX() - blockPos.getX();
                                    calcDif -= 0.5D;

                                    if (calcDif < 0)
                                        calcDif *= -1;
                                    calcDif -= 0.5;

                                    if (calcDif < dif)
                                        dif = calcDif;
                                }

                            }
                            case 1: {
                                final WBlockPos blockPos = new WBlockPos(MinecraftInstance.mc.getThePlayer().getPosX() + 1, MinecraftInstance.mc.getThePlayer().getPosY() - (MinecraftInstance.mc.getThePlayer().getPosY() == (int) MinecraftInstance.mc.getThePlayer().getPosY() + 0.5D ? 0D : 1.0D), MinecraftInstance.mc.getThePlayer().getPosZ());
                                final PlaceInfo placeInfo = PlaceInfo.get(blockPos);

                                if (BlockUtils.isReplaceable(blockPos) && placeInfo != null) {
                                    double calcDif = MinecraftInstance.mc.getThePlayer().getPosX() - blockPos.getX();
                                    calcDif -= 0.5D;

                                    if (calcDif < 0)
                                        calcDif *= -1;
                                    calcDif -= 0.5;

                                    if (calcDif < dif)
                                        dif = calcDif;
                                }

                            }
                            case 2: {
                                final WBlockPos blockPos = new WBlockPos(MinecraftInstance.mc.getThePlayer().getPosX(), MinecraftInstance.mc.getThePlayer().getPosY() - (MinecraftInstance.mc.getThePlayer().getPosY() == (int) MinecraftInstance.mc.getThePlayer().getPosY() + 0.5D ? 0D : 1.0D), MinecraftInstance.mc.getThePlayer().getPosZ() - 1);
                                final PlaceInfo placeInfo = PlaceInfo.get(blockPos);

                                if (BlockUtils.isReplaceable(blockPos) && placeInfo != null) {
                                    double calcDif = MinecraftInstance.mc.getThePlayer().getPosZ() - blockPos.getZ();
                                    calcDif -= 0.5D;

                                    if (calcDif < 0)
                                        calcDif *= -1;
                                    calcDif -= 0.5;

                                    if (calcDif < dif)
                                        dif = calcDif;
                                }

                            }
                            case 3: {
                                final WBlockPos blockPos = new WBlockPos(MinecraftInstance.mc.getThePlayer().getPosX(), MinecraftInstance.mc.getThePlayer().getPosY() - (MinecraftInstance.mc.getThePlayer().getPosY() == (int) MinecraftInstance.mc.getThePlayer().getPosY() + 0.5D ? 0D : 1.0D), MinecraftInstance.mc.getThePlayer().getPosZ() + 1);
                                final PlaceInfo placeInfo = PlaceInfo.get(blockPos);

                                if (BlockUtils.isReplaceable(blockPos) && placeInfo != null) {
                                    double calcDif = MinecraftInstance.mc.getThePlayer().getPosZ() - blockPos.getZ();
                                    calcDif -= 0.5D;

                                    if (calcDif < 0)
                                        calcDif *= -1;
                                    calcDif -= 0.5;

                                    if (calcDif < dif)
                                        dif = calcDif;
                                }

                            }
                        }
                    }
                }

                if (placedBlocksWithoutEagle >= blocksToEagleValue.get()) {
                    final boolean shouldEagle = MinecraftInstance.mc.getTheWorld().getBlockState(new WBlockPos(MinecraftInstance.mc.getThePlayer().getPosX(),
                            MinecraftInstance.mc.getThePlayer().getPosY() - 1D, MinecraftInstance.mc.getThePlayer().getPosZ())).getBlock().equals(MinecraftInstance.classProvider.getBlockEnum(BlockType.AIR)) || (dif < edgeDistanceValue.get() && eagleValue.get().equalsIgnoreCase("EdgeDistance"));

                    if (eagleValue.get().equalsIgnoreCase("Silent") && !shouldGoDown) {
                        if (eagleSneaking != shouldEagle) {
                            MinecraftInstance.mc.getNetHandler().addToSendQueue(
                                    MinecraftInstance.classProvider.createCPacketEntityAction(MinecraftInstance.mc.getThePlayer(), shouldEagle ?
                                            ICPacketEntityAction.WAction.START_SNEAKING :
                                            ICPacketEntityAction.WAction.STOP_SNEAKING)
                            );
                        }

                        eagleSneaking = shouldEagle;
                    } else
                        MinecraftInstance.mc.getGameSettings().getKeyBindSneak().setPressed(shouldEagle);

                    placedBlocksWithoutEagle = 0;
                } else
                    placedBlocksWithoutEagle++;
            }

            // Zitter
            if (zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("teleport")) {
                MovementUtils.strafe(zitterSpeed.get());


                final double yaw = Math.toRadians(MinecraftInstance.mc.getThePlayer().getRotationYaw() + (zitterDirection ? 90D : -90D));
                MinecraftInstance.mc.getThePlayer().setMotionX(MinecraftInstance.mc.getThePlayer().getMotionX() - Math.sin(yaw) * zitterStrength.get());
                MinecraftInstance.mc.getThePlayer().setMotionZ(MinecraftInstance.mc.getThePlayer().getMotionZ() + Math.cos(yaw) * zitterStrength.get());
                zitterDirection = !zitterDirection;
            }
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (MinecraftInstance.mc.getThePlayer() == null)
            return;

        final IPacket packet = event.getPacket();

        // AutoBlock
        if (MinecraftInstance.classProvider.isCPacketHeldItemChange(packet)) {
            final ICPacketHeldItemChange packetHeldItemChange = packet.asCPacketHeldItemChange();

            slot = packetHeldItemChange.getSlotId();
        }
    }

    @EventTarget
    private void onStrafe(StrafeEvent event) {

        if (!rotationStrafeValue.get())
            return;
        RotationUtils.serverRotation.applyStrafeToPlayer(event);
        event.cancelEvent();
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        final EventState eventState = event.getEventState();

        // Lock Rotation
        if (!rotationModeValue.get().equalsIgnoreCase("Off") && keepRotationValue.get() && lockRotation != null)
            setRotation(lockRotation);


        if ((facesBlock || rotationModeValue.get().equalsIgnoreCase("Off")) && placeModeValue.get().equalsIgnoreCase(eventState.getStateName()))
            place();

        // Update and search for new block
        if (eventState == EventState.PRE)
            update();

        // Reset placeable delay
        if (targetPlace == null && placeableDelay.get())
            delayTimer.reset();
    }

    private void update() {
        final boolean isHeldItemBlock = MinecraftInstance.mc.getThePlayer().getHeldItem() != null && MinecraftInstance.classProvider.isItemBlock(MinecraftInstance.mc.getThePlayer().getHeldItem().getItem());
        if (!autoBlockValue.get().equalsIgnoreCase("Off") ? InventoryUtils.findAutoBlockBlock() == -1 && !isHeldItemBlock : !isHeldItemBlock)
            return;

        findBlock(modeValue.get().equalsIgnoreCase("expand"));
    }

    private void setRotation(Rotation rotation, int keepRotation) {
        if (silentRotation.get()) {
            RotationUtils.setTargetRotation(rotation, keepRotation);
        } else {
            MinecraftInstance.mc.getThePlayer().setRotationYaw(rotation.getYaw());
            MinecraftInstance.mc.getThePlayer().setRotationPitch(rotation.getPitch());
        }
    }

    private void setRotation(Rotation rotation) {
        setRotation(rotation, 0);
    }

    /**
     * Search for new target block
     */
    private void findBlock(final boolean expand) {
        final WBlockPos blockPosition = shouldGoDown ? (MinecraftInstance.mc.getThePlayer().getPosY() == (int) MinecraftInstance.mc.getThePlayer().getPosY() + 0.5D ?
                new WBlockPos(MinecraftInstance.mc.getThePlayer().getPosX(), MinecraftInstance.mc.getThePlayer().getPosY() - 0.6D, MinecraftInstance.mc.getThePlayer().getPosZ())
                : new WBlockPos(MinecraftInstance.mc.getThePlayer().getPosX(), MinecraftInstance.mc.getThePlayer().getPosY() - 0.6, MinecraftInstance.mc.getThePlayer().getPosZ()).down()) :
                (MinecraftInstance.mc.getThePlayer().getPosY() == (int) MinecraftInstance.mc.getThePlayer().getPosY() + 0.5D ? new WBlockPos(MinecraftInstance.mc.getThePlayer())
                        : new WBlockPos(MinecraftInstance.mc.getThePlayer().getPosX(), MinecraftInstance.mc.getThePlayer().getPosY(), MinecraftInstance.mc.getThePlayer().getPosZ()).down());

        if (!expand && (!BlockUtils.isReplaceable(blockPosition) || search(blockPosition, !shouldGoDown)))
            return;

        if (expand) {
            for (int i = 0; i < expandLengthValue.get(); i++) {
                if (search(blockPosition.add(
                        MinecraftInstance.mc.getThePlayer().getHorizontalFacing().equals(MinecraftInstance.classProvider.getEnumFacing(EnumFacingType.WEST)) ? -i : MinecraftInstance.mc.getThePlayer().getHorizontalFacing().equals(MinecraftInstance.classProvider.getEnumFacing(EnumFacingType.EAST)) ? i : 0,
                        0,
                        MinecraftInstance.mc.getThePlayer().getHorizontalFacing().equals(MinecraftInstance.classProvider.getEnumFacing(EnumFacingType.NORTH)) ? -i : MinecraftInstance.mc.getThePlayer().getHorizontalFacing().equals(MinecraftInstance.classProvider.getEnumFacing(EnumFacingType.SOUTH)) ? i : 0
                ), false))

                    return;
            }
        } else if (searchValue.get()) {
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    if (search(blockPosition.add(x, 0, z), !shouldGoDown))
                        return;
        }
    }

    /**
     * Place target block
     */
    private void place() {
        if (targetPlace == null) {
            if (placeableDelay.get())
                delayTimer.reset();
            return;
        }

        if (!delayTimer.hasTimePassed(delay) || (sameYValue.get() && launchY - 1 != (int) targetPlace.getVec3().getYCoord()))
            return;

        int blockSlot = -1;
        IItemStack itemStack = MinecraftInstance.mc.getThePlayer().getHeldItem();

        if (itemStack == null || !MinecraftInstance.classProvider.isItemBlock(itemStack.getItem()) ||
                MinecraftInstance.classProvider.isBlockBush(itemStack.getItem().asItemBlock().getBlock()) || MinecraftInstance.mc.getThePlayer().getHeldItem().getStackSize() <= 0) {
            if (autoBlockValue.get().equalsIgnoreCase("Off"))
                return;

            blockSlot = InventoryUtils.findAutoBlockBlock();

            if (blockSlot == -1)
                return;


            if (autoBlockValue.get().equalsIgnoreCase("Spoof")) {
                if (blockSlot - 36 != slot)
                    MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketHeldItemChange(blockSlot - 36));
            } else if(autoBlockValue.get().equalsIgnoreCase("Switch")) {
                MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketHeldItemChange(blockSlot - 36));
            } else {
                MinecraftInstance.mc.getThePlayer().getInventory().setCurrentItem(blockSlot - 36);
                MinecraftInstance.mc.getPlayerController().updateController();
            }
            itemStack = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(blockSlot).getStack();
        }


        if (MinecraftInstance.mc.getPlayerController().onPlayerRightClick(MinecraftInstance.mc.getThePlayer(), MinecraftInstance.mc.getTheWorld(), itemStack, targetPlace.getBlockPos(), targetPlace.getEnumFacing(), targetPlace.getVec3())) {
            delayTimer.reset();
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());

            if (MinecraftInstance.mc.getThePlayer().getOnGround()) {
                final float modifier = speedModifierValue.get();

                MinecraftInstance.mc.getThePlayer().setMotionX(MinecraftInstance.mc.getThePlayer().getMotionX() * modifier);
                MinecraftInstance.mc.getThePlayer().setMotionZ(MinecraftInstance.mc.getThePlayer().getMotionZ() * modifier);
            }

            if (swingValue.get())
                MinecraftInstance.mc.getThePlayer().swingItem();
            else
                MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketAnimation());
        }

        /*
        if (!stayAutoBlock.get() && blockSlot >= 0)
            mc.getNetHandler().addToSendQueue(classProvider.createCPacketHeldItemChange(mc.getThePlayer().getInventory().getCurrentItem()));
         */

        // Reset
        this.targetPlace = null;
    }

    /**
     * Disable scaffold module
     */
    @Override
    public void onDisable() {
        if (MinecraftInstance.mc.getThePlayer() == null) return;

        if (!MinecraftInstance.mc.getGameSettings().isKeyDown(MinecraftInstance.mc.getGameSettings().getKeyBindSneak())) {
            MinecraftInstance.mc.getGameSettings().getKeyBindSneak().setPressed(false);

            if (eagleSneaking)
                MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketEntityAction(MinecraftInstance.mc.getThePlayer(), ICPacketEntityAction.WAction.STOP_SNEAKING));
        }

        if (!MinecraftInstance.mc.getGameSettings().isKeyDown(MinecraftInstance.mc.getGameSettings().getKeyBindRight()))
            MinecraftInstance.mc.getGameSettings().getKeyBindRight().setPressed(false);

        if (!MinecraftInstance.mc.getGameSettings().isKeyDown(MinecraftInstance.mc.getGameSettings().getKeyBindLeft()))
            MinecraftInstance.mc.getGameSettings().getKeyBindLeft().setPressed(false);

        lockRotation = null;
        limitedRotation = null;
        facesBlock = false;
        MinecraftInstance.mc.getTimer().setTimerSpeed(1F);
        shouldGoDown = false;

        if (slot != MinecraftInstance.mc.getThePlayer().getInventory().getCurrentItem())
            MinecraftInstance.mc.getNetHandler().addToSendQueue(MinecraftInstance.classProvider.createCPacketHeldItemChange(MinecraftInstance.mc.getThePlayer().getInventory().getCurrentItem()));
    }

    /**
     * Entity movement event
     *
     * @param event
     */
    @EventTarget
    public void onMove(final MoveEvent event) {
        if (!safeWalkValue.get() || shouldGoDown)
            return;

        if (airSafeValue.get() || MinecraftInstance.mc.getThePlayer().getOnGround())
            event.setSafeWalk(true);
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        if (counterDisplayValue.get()) {
            GL11.glPushMatrix();

            final BlockOverlay blockOverlay = (BlockOverlay) DragLine.moduleManager.getModule(BlockOverlay.class);
            if (blockOverlay.getState() && blockOverlay.getInfoValue().get() && blockOverlay.getCurrentBlock() != null)
                GL11.glTranslatef(0, 15F, 0);

            final String info = "Blocks: §7" + getBlocksAmount();
            final IScaledResolution scaledResolution = MinecraftInstance.classProvider.createScaledResolution(MinecraftInstance.mc);

            RenderUtils.drawBorderedRect((scaledResolution.getScaledWidth() / 2.0f) - 2,
                    (scaledResolution.getScaledHeight() / 2.0f) + 5,
                    (scaledResolution.getScaledWidth() / 2.0f) + Fonts.font40.getStringWidth(info) + 2,
                    (scaledResolution.getScaledHeight() / 2.0f) + 16, 3, Color.BLACK.getRGB(), Color.BLACK.getRGB());

            MinecraftInstance.classProvider.getGlStateManager().resetColor();

            Fonts.font40.drawString(info, scaledResolution.getScaledWidth() / 2.0f,
                    scaledResolution.getScaledHeight() / 2.0f + 7, Color.WHITE.getRGB());

            GL11.glPopMatrix();
        }
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (!markValue.get())
            return;

        for (int i = 0; i < (modeValue.get().equalsIgnoreCase("Expand") ? expandLengthValue.get() + 1 : 2); i++) {
            final WBlockPos blockPos = new WBlockPos(MinecraftInstance.mc.getThePlayer().getPosX() + (MinecraftInstance.mc.getThePlayer().getHorizontalFacing().equals(MinecraftInstance.classProvider.getEnumFacing(EnumFacingType.WEST)) ? -i : MinecraftInstance.mc.getThePlayer().getHorizontalFacing().equals(MinecraftInstance.classProvider.getEnumFacing(EnumFacingType.EAST)) ? i : 0), MinecraftInstance.mc.getThePlayer().getPosY() - (MinecraftInstance.mc.getThePlayer().getPosY() == (int) MinecraftInstance.mc.getThePlayer().getPosY() + 0.5D ? 0D : 1.0D) - (shouldGoDown ? 1D : 0), MinecraftInstance.mc.getThePlayer().getPosZ() + (MinecraftInstance.mc.getThePlayer().getHorizontalFacing().equals(MinecraftInstance.classProvider.getEnumFacing(EnumFacingType.NORTH)) ? -i : MinecraftInstance.mc.getThePlayer().getHorizontalFacing().equals(MinecraftInstance.classProvider.getEnumFacing(EnumFacingType.SOUTH)) ? i : 0));
            final PlaceInfo placeInfo = PlaceInfo.get(blockPos);

            if (BlockUtils.isReplaceable(blockPos) && placeInfo != null) {
                RenderUtils.drawBlockBox(blockPos, new Color(68, 117, 255, 100), false);
                break;
            }
        }
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */
    private boolean search(final WBlockPos blockPosition, final boolean checks) {
        if (!BlockUtils.isReplaceable(blockPosition))
            return false;
        // StaticModes
        final boolean staticMode = rotationModeValue.get().equalsIgnoreCase("Static");
        final boolean staticPitchMode = staticMode || rotationModeValue.get().equalsIgnoreCase("StaticPitch");
        final boolean staticYawMode = staticMode || rotationModeValue.get().equalsIgnoreCase("StaticYaw");
        final float staticPitch = staticPitchValue.get();
        final float staticYawOffset = staticYawOffsetValue.get();

        // SearchRanges
        final double xzRV = xzRangeValue.get();
        final double xzSSV = calcStepSize(xzRV);
        final double yRV = yRangeValue.get();
        final double ySSV = calcStepSize(yRV);

        double xSearchFace = 0;
        double ySearchFace = 0;
        double zSearchFace = 0;


        final WVec3 eyesPos = new WVec3(MinecraftInstance.mc.getThePlayer().getPosX(), MinecraftInstance.mc.getThePlayer().getEntityBoundingBox().getMinY() + MinecraftInstance.mc.getThePlayer().getEyeHeight(), MinecraftInstance.mc.getThePlayer().getPosZ());

        PlaceRotation placeRotation = null;

        for (final EnumFacingType facingType : EnumFacingType.values()) {
            IEnumFacing side = MinecraftInstance.classProvider.getEnumFacing(facingType);
            final WBlockPos neighbor = blockPosition.offset(side);

            if (!BlockUtils.canBeClicked(neighbor))
                continue;

            final WVec3 dirVec = new WVec3(side.getDirectionVec());

            for (double xSearch = 0.5D - (xzRV / 2); xSearch <= 0.5D + (xzRV / 2); xSearch += xzSSV) {
                for (double ySearch = 0.5D - (yRV / 2); ySearch <= 0.5D + (yRV / 2); ySearch += ySSV) {
                    for (double zSearch = 0.5D - (xzRV / 2); zSearch <= 0.5D + (xzRV / 2); zSearch += xzSSV) {
                        final WVec3 posVec = new WVec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        final WVec3 hitVec = posVec.add(new WVec3(dirVec.getXCoord() * 0.5, dirVec.getYCoord() * 0.5, dirVec.getZCoord() * 0.5));

                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18D || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || MinecraftInstance.mc.getTheWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) != null))
                            continue;

                        // face block
                        for (int i = 0; i < (staticYawMode ? 2 : 1); i++) {
                            final double diffX = staticYawMode && i == 0 ? 0 : hitVec.getXCoord() - eyesPos.getXCoord();
                            final double diffY = hitVec.getYCoord() - eyesPos.getYCoord();
                            final double diffZ = staticYawMode && i == 1 ? 0 : hitVec.getZCoord() - eyesPos.getZCoord();

                            final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

                            final float pitch = staticPitchMode ? staticPitch : WMathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)));
                            final Rotation rotation = new Rotation(
                                    WMathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F +
                                            (staticYawMode ? staticYawOffset : 0)), pitch);

                            final WVec3 rotationVector = RotationUtils.getVectorForRotation(rotation);
                            final WVec3 vector = eyesPos.addVector(rotationVector.getXCoord() * 4, rotationVector.getYCoord() * 4, rotationVector.getZCoord() * 4);
                            final IMovingObjectPosition obj = MinecraftInstance.mc.getTheWorld().rayTraceBlocks(eyesPos, vector, false, false, true);

                            if (obj.getTypeOfHit() != IMovingObjectPosition.WMovingObjectType.BLOCK || !obj.getBlockPos().equals(neighbor))
                                continue;

                            if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(placeRotation.getRotation())) {
                                placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                            }
                            xSearchFace = xSearch;
                            ySearchFace = ySearch;
                            zSearchFace = zSearch;
                        }
                    }
                }
            }
        }

        if (placeRotation == null) return false;

        if (!rotationModeValue.get().equalsIgnoreCase("Off")) {
            if (minTurnSpeedValue.get() < 180) {
                limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, placeRotation.getRotation(), (float) (Math.random() * (maxTurnSpeedValue.get() - minTurnSpeedValue.get()) + minTurnSpeedValue.get()));
                setRotation(limitedRotation, keepLengthValue.get());
                lockRotation = limitedRotation;

                facesBlock = false;
                for (final EnumFacingType facingType : EnumFacingType.values()) {
                    IEnumFacing side = MinecraftInstance.classProvider.getEnumFacing(facingType);
                    final WBlockPos neighbor = blockPosition.offset(side);

                    if (!BlockUtils.canBeClicked(neighbor))
                        continue;

                    final WVec3 dirVec = new WVec3(side.getDirectionVec());

                    final WVec3 posVec = new WVec3(blockPosition).addVector(xSearchFace, ySearchFace, zSearchFace);
                    final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                    final WVec3 hitVec = posVec.add(new WVec3(dirVec.getXCoord() * 0.5, dirVec.getYCoord() * 0.5, dirVec.getZCoord() * 0.5));

                    if (checks && (eyesPos.squareDistanceTo(hitVec) > 18D || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || MinecraftInstance.mc.getTheWorld().rayTraceBlocks(eyesPos, hitVec, false, true, false) != null))
                        continue;

                    final WVec3 rotationVector = RotationUtils.getVectorForRotation(limitedRotation);
                    final WVec3 vector = eyesPos.addVector(rotationVector.getXCoord() * 4, rotationVector.getYCoord() * 4, rotationVector.getZCoord() * 4);
                    final IMovingObjectPosition obj = MinecraftInstance.mc.getTheWorld().rayTraceBlocks(eyesPos, vector, false, false, true);

                    if (!(obj.getTypeOfHit() == IMovingObjectPosition.WMovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor)))
                        continue;
                    facesBlock = true;
                    break;
                }
            } else {
                setRotation(placeRotation.getRotation(), keepLengthValue.get());
                lockRotation = placeRotation.getRotation();
                facesBlock = true;
            }
        }
        targetPlace = placeRotation.getPlaceInfo();
        return true;
    }

    private double calcStepSize(double range) {
        double accuracy = searchAccuracyValue.get();
        accuracy += accuracy % 2; // If it is set to uneven it changes it to even. Fixes a bug
        if (range / accuracy < 0.01D)
            return 0.01D;
        return range / accuracy;
    }

    /**
     * @return hotbar blocks amount
     */
    private int getBlocksAmount() {
        int amount = 0;

        for (int i = 36; i < 45; i++) {
            final IItemStack itemStack = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getStack();

            if (itemStack != null && MinecraftInstance.classProvider.isItemBlock(itemStack.getItem())) {
                final IBlock block = (itemStack.getItem().asItemBlock()).getBlock();

                IItemStack heldItem = MinecraftInstance.mc.getThePlayer().getHeldItem();

                if (heldItem != null && heldItem.equals(itemStack) || !InventoryUtils.BLOCK_BLACKLIST.contains(block) && !MinecraftInstance.classProvider.isBlockBush(block))
                    amount += itemStack.getStackSize();
            }
        }

        return amount;
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }

    public void getBestBlocks(){

        if(getBlocksAmount() == 0)
            return;
        if(picker.get()){
            int bestInvSlot = getBiggestBlockSlotInv();
            int bestHotbarSlot = getBiggestBlockSlotHotbar();
            int bestSlot = getBiggestBlockSlotHotbar() > 0 ? getBiggestBlockSlotHotbar() : getBiggestBlockSlotInv();
            int spoofSlot = 42;
            if(bestHotbarSlot > 0 && bestInvSlot > 0){
                if (MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(bestInvSlot).getHasStack() && MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(bestHotbarSlot).getHasStack() ) {
                    if(MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(bestHotbarSlot).getStack().getStackSize() < MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(bestInvSlot).getStack().getStackSize()){
                        bestSlot = bestInvSlot;
                    }
                }
            }
            if(hotbarContainBlock()){
                for (int a = 36; a < 45; a++) {
                    if (MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(a).getHasStack()) {
                        IItem item = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(a).getStack().getItem();
                        if(item instanceof IItemBlock){
                            spoofSlot = a;
                            break;
                        }
                    }
                }
            }else{
                for (int a = 36; a < 45; a++) {
                    if (!MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(a).getHasStack()) {
                        spoofSlot = a;
                        break;
                    }
                }
            }

            if (MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(spoofSlot).getSlotNumber() != bestSlot) {

                swap(bestSlot, spoofSlot - 36);
                MinecraftInstance.mc.getPlayerController().updateController();


            }
        }else{
            if (invCheck()) {

                IItemStack is = (IItemStack) MinecraftInstance.functions.getItemById(261);
                for (int i = 9; i < 36; i++) {

                    if (MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getHasStack()) {
                        IItem item = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getStack().getItem();
                        int count = 0;
                        if (item instanceof IItemBlock) {
                            for (int a = 36; a < 45; a++) {
                                if (MinecraftInstance.functions.canAddItemToSlot(MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(a), is, true)) {
                                    swap(i, a - 36);
                                    count++;
                                    break;
                                }
                            }

                            if (count == 0) {

                                swap(i, 7);
                            }
                            break;

                        }
                    }
                }
            }
        }
    }

    private boolean hotbarContainBlock() {
        int i = 36;

        while (i < 45) {
            try {
                IItemStack stack = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getStack();
                if ((stack == null) || (stack.getItem() == null) || !(stack.getItem() instanceof IItemBlock)) {
                    i++;
                    continue;
                }
                return true;
            } catch (Exception e) {

            }
        }

        return false;

    }

    public int getBiggestBlockSlotHotbar(){
        int slot = -1;
        int size = 0;
        if(getBlocksAmount() == 0)
            return - 1;
        for (int i = 36; i < 45; i++) {
            if (MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getHasStack()) {
                IItem item = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getStack().getItem();
                IItemStack is = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getStack();
                if (item instanceof IItemBlock) {
                    if(is.getStackSize() > size){
                        size = is.getStackSize();
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }

    protected void swap(int slot, int hotbarNum) {

        MinecraftInstance.mc.getPlayerController().windowClick(MinecraftInstance.mc.getThePlayer().getInventoryContainer().getWindowId(), slot, hotbarNum,2, MinecraftInstance.mc.getThePlayer());

    }

    private boolean invCheck() {
        for (int i = 36; i < 45; i++) {
            if (MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getHasStack()) {
                IItem item = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getStack().getItem();
                if (item instanceof IItemBlock) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getBiggestBlockSlotInv(){
        int slot = -1;
        int size = 0;
        if(getBlocksAmount() == 0)
            return - 1;
        for (int i = 9; i < 36; i++) {
            if (MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getHasStack()) {
                IItem item = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getStack().getItem();
                IItemStack is = MinecraftInstance.mc.getThePlayer().getInventoryContainer().getSlot(i).getStack();
                if (item instanceof IItemBlock) {
                    if(is.getStackSize() > size){
                        size = is.getStackSize();
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }


}
