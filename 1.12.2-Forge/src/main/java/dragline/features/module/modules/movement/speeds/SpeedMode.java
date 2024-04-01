package dragline.features.module.modules.movement.speeds;

import dragline.DragLine;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import dragline.event.MoveEvent;
import dragline.features.module.modules.movement.Speed;
import dragline.utils.MinecraftInstance;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public abstract class SpeedMode extends MinecraftInstance {

    public final String modeName;

    public SpeedMode(final String modeName) {
        this.modeName = modeName;
    }

    private boolean isActive() {
        final Speed speed = ((Speed) DragLine.moduleManager.getModule(Speed.class));

        return speed != null && !Objects.requireNonNull(mc.getThePlayer()).isSneaking() && speed.getState() && speed.modeValue.get().equals(modeName);
    }

    public void onMotion() {

    }

    public void onUpdate() {

    }

    public void onMove(final MoveEvent event) {

    }

    public void onTick() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }
}
