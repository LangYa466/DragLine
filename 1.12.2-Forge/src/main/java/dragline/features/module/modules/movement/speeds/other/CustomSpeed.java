/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */

package dragline.features.module.modules.movement.speeds.other;


import dragline.DragLine;
import dragline.event.MoveEvent;
import dragline.features.module.modules.movement.Speed;
import dragline.features.module.modules.movement.speeds.SpeedMode;
import dragline.utils.MovementUtils;

public class CustomSpeed extends SpeedMode {

    public CustomSpeed() {
        super("Custom");
    }

    @Override
    public void onMotion() {
        if (MovementUtils.isMoving()) {
            final Speed speed = (Speed) DragLine.moduleManager.getModule(Speed.class);

            if (speed == null)
                return;

            mc.getTimer().setTimerSpeed(speed.customTimerValue.get());

            if (mc2.player.onGround) {
                MovementUtils.strafe(speed.customSpeedValue.get());
                mc2.player.motionY = speed.customYValue.get();
            } else if (speed.customStrafeValue.get())
                MovementUtils.strafe(speed.customSpeedValue.get());
            else
                MovementUtils.strafe();
        } else
            mc2.player.motionX = mc2.player.motionZ = 0D;
    }

    @Override
    public void onEnable() {
        final Speed speed = (Speed) DragLine.moduleManager.getModule(Speed.class);

        if (speed == null)
            return;

        if (speed.resetXZValue.get()) mc2.player.motionX = mc2.player.motionZ = 0D;
        if (speed.resetYValue.get()) mc2.player.motionY = 0D;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.getTimer().setTimerSpeed(1F);
        super.onDisable();
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMove(MoveEvent event) {
    }
}
