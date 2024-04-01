package dragline.features.module.modules.client;

import dragline.utils.MinecraftInstance;
import dragline.DragLine;
import dragline.api.minecraft.network.IPacket;
import dragline.event.EventTarget;
import dragline.event.PacketEvent;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.ModuleInfo;
import dragline.ui.client.clickgui.style.styles.LiquidBounceStyle;
import dragline.ui.client.clickgui.style.styles.NullStyle;
import dragline.ui.client.clickgui.style.styles.SlowlyStyle;
import dragline.utils.render.ColorUtils;
import dragline.value.BoolValue;
import dragline.value.FloatValue;
import dragline.value.IntegerValue;
import dragline.value.ListValue;
import me.nelly.ui.novoline.ClickyUI;
import org.lwjgl.input.Keyboard;
import sk1d.unknow.ui.newdropdown.DropdownClickGui;

import java.awt.*;

@ModuleInfo(name = "ClickGUI", description = "Opens the ClickGUI.", category = ModuleCategory.CLIENT, keyBind = Keyboard.KEY_RSHIFT, canEnable = false)
public class ClickGUI extends Module {
    private final ListValue styleValue = new ListValue("Style", new String[] {"LiquidBounce", "Null", "Slowly"}, "Slowly") {
        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            updateStyle();
        }

    };
    private final ListValue clickguimodeValue = new ListValue("Mode", new String[] {"LiquidBounce","Novoline"}, "LiquidBounce");
    public static final ListValue colormode = new ListValue("Setting Accent", new String[]{"White", "Color"},"Color");
    public static final ListValue scrollMode = new ListValue("Scroll Mode", new String[]{"Screen Height", "Value"},"Value");
    public static final IntegerValue clickHeight = new IntegerValue("Tab Height", 250, 100, 500);
    public final FloatValue scaleValue = new FloatValue("Scale", 1F, 0.7F, 2F);
    public final IntegerValue maxElementsValue = new IntegerValue("MaxElements", 15, 1, 20);

    private static final IntegerValue colorRedValue = new IntegerValue("R", 0, 0, 255);
    private static final IntegerValue colorGreenValue = new IntegerValue("G", 160, 0, 255);
    private static final IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private static final BoolValue colorRainbow = new BoolValue("Rainbow", false);
    public static final BoolValue backback = new BoolValue("Background Accent",true);
    public static final BoolValue fastRenderValue = new BoolValue("FastRender", true);

    public static Color generateColor() {
        return colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
    }
    public static int generateRGB() {
        return colorRainbow.get() ? ColorUtils.rainbow().getRGB() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()).getRGB();
    }

    @Override
    public void onEnable() {
        if (clickguimodeValue.get().equalsIgnoreCase("LiquidBounce")) {
            updateStyle();
            MinecraftInstance.mc.displayGuiScreen(MinecraftInstance.classProvider.wrapGuiScreen(DragLine.clickGui));
        }
        if (clickguimodeValue.get().equalsIgnoreCase("NovoLine")) {
            MinecraftInstance.mc2.displayGuiScreen(new ClickyUI());
        }



    }

    private void updateStyle() {
        switch(styleValue.get().toLowerCase()) {
            case "liquidbounce":
                DragLine.clickGui.style = new LiquidBounceStyle();
                break;
            case "null":
                DragLine.clickGui.style = new NullStyle();
                break;
            case "slowly":
                DragLine.clickGui.style = new SlowlyStyle();
                break;
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onPacket(final PacketEvent event) {
        final IPacket packet = event.getPacket();

        if (MinecraftInstance.classProvider.isSPacketCloseWindow(packet) && MinecraftInstance.classProvider.isClickGui(MinecraftInstance.mc.getCurrentScreen())) {
            event.cancelEvent();
        }
    }
}
