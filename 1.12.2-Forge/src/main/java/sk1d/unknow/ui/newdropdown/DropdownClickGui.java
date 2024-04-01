package sk1d.unknow.ui.newdropdown;

import dragline.DragLine;
import dragline.api.minecraft.util.IResourceLocation;
import dragline.api.minecraft.util.IScaledResolution;
import dragline.api.util.WrappedGuiScreen;
import dragline.features.module.ModuleCategory;
import dragline.ui.client.hud.designer.GuiHudDesigner;
import sk1d.unknow.ui.newdropdown.impl.SettingComponents;
import sk1d.unknow.ui.newdropdown.utils.animations.Animation;
import sk1d.unknow.ui.newdropdown.utils.animations.Direction;
import sk1d.unknow.ui.newdropdown.utils.animations.impl.DecelerateAnimation;
import sk1d.unknow.ui.newdropdown.utils.animations.impl.EaseBackIn;
import sk1d.unknow.ui.newdropdown.utils.normal.Main;
import sk1d.unknow.ui.newdropdown.utils.render.DrRenderUtils;
import dragline.utils.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import sk1d.unknow.utils.ClientMain;

import java.util.ArrayList;
import java.util.List;

public class DropdownClickGui extends WrappedGuiScreen {
    private Animation openingAnimation;
    private EaseBackIn fadeAnimation;
    private final IResourceLocation hudIcon = classProvider.createResourceLocation("liquidbounce/custom_hud_icon.png");

    private DecelerateAnimation configHover;

    public List<MainScreen> categoryPanels;

    @Override
    public void initGui() {
        if (categoryPanels == null || Main.reloadModules) {
            categoryPanels = new ArrayList() {{
                for (ModuleCategory category : ModuleCategory.values()) {
                    //

                    add(new MainScreen(category));
                }
            }};
            Main.reloadModules = false;
        }
        ClientMain.getInstance().getSideGui().initGui();
        fadeAnimation = new EaseBackIn(400, 1, 2f);
        openingAnimation = new EaseBackIn(400, .4f, 2f);
        configHover = new DecelerateAnimation(250, 1);

        for (MainScreen catPanels : categoryPanels) {
            catPanels.animation = fadeAnimation;
            catPanels.openingAnimation = openingAnimation;
            catPanels.initGui();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            openingAnimation.setDirection(Direction.BACKWARDS);
            ClientMain.getInstance().getSideGui().focused = false;
            fadeAnimation.setDirection(openingAnimation.getDirection());
        }
        ClientMain.getInstance().getSideGui().keyTyped(typedChar, keyCode);
        categoryPanels.forEach(categoryPanel -> categoryPanel.keyTyped(typedChar, keyCode));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Mouse.isButtonDown(0) && mouseX >= 5 && mouseX <= 50 && mouseY <= representedScreen.getHeight() - 5 && mouseY >= representedScreen.getHeight() - 50)
        mc.displayGuiScreen(classProvider.wrapGuiScreen(new GuiHudDesigner()));
        RenderUtils.drawImage(hudIcon, 9, representedScreen.getHeight() - 41, 32, 32);

        if (Main.reloadModules) {
            initGui();
        }
     //   if (Main.walk.isEnabled()) {
    //        InventoryMove.updateStates();
   //     }

        //If the closing animation finished then change the gui screen to null
        if (openingAnimation.isDone() && openingAnimation.getDirection().equals(Direction.BACKWARDS)) {
            mc.displayGuiScreen(null);
            return;
        }

        boolean focusedConfigGui = ClientMain.getInstance().getSideGui().focused;
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;
        final IScaledResolution sr = DragLine.INSTANCE.getWrapper().getClassProvider().createScaledResolution(mc);

        boolean hoveringConfig = DrRenderUtils.isHovering(representedScreen.getWidth() - 120, representedScreen.getHeight() - 65, 75, 25, fakeMouseX, fakeMouseY);

        configHover.setDirection(hoveringConfig ? Direction.FORWARDS : Direction.BACKWARDS);
        int alphaAnimation = Math.max(0, Math.min(255, (int) (255 * fadeAnimation.getOutput())));

        GlStateManager.color(1, 1, 1, 1);

        SettingComponents.scale = (float) (openingAnimation.getOutput() + .6f);
        DrRenderUtils.scale(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, (float) openingAnimation.getOutput() + .6f, () -> {
            for (MainScreen catPanels : categoryPanels) {
                catPanels.drawScreen(fakeMouseX, fakeMouseY);
            }

            ClientMain.getInstance().getSideGui().drawScreen(mouseX, mouseY, partialTicks, alphaAnimation);
        });


    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean focused = ClientMain.getInstance().getSideGui().focused;
        ClientMain.getInstance().getSideGui().mouseClicked(mouseX, mouseY, mouseButton);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        boolean focused = ClientMain.getInstance().getSideGui().focused;
        ClientMain.getInstance().getSideGui().mouseReleased(mouseX, mouseY, state);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseReleased(mouseX, mouseY, state));
        }
    }

}
