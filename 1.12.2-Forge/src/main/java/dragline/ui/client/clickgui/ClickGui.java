/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.ui.client.clickgui;

import dragline.DragLine;
import dragline.api.minecraft.util.IResourceLocation;
import dragline.api.util.WrappedGuiScreen;
import dragline.features.module.Module;
import dragline.features.module.ModuleCategory;
import dragline.features.module.modules.client.ClickGUI;
import dragline.ui.client.clickgui.elements.ButtonElement;
import dragline.ui.client.clickgui.elements.Element;
import dragline.ui.client.clickgui.elements.ModuleElement;
import dragline.ui.client.clickgui.style.Style;
import dragline.ui.client.clickgui.style.styles.SlowlyStyle;
import dragline.ui.client.hud.designer.GuiHudDesigner;
import dragline.utils.EntityUtils;
import dragline.utils.render.RenderUtils;
import me.paimon.utils.ClickEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class ClickGui extends WrappedGuiScreen {
    private final List<ClickEffect> clickEffects = new ArrayList<>();
    public final List<Panel> panels = new ArrayList<>();
    private final IResourceLocation hudIcon = classProvider.createResourceLocation("liquidbounce/custom_hud_icon.png");
    public Style style = new SlowlyStyle();
    private Panel clickedPanel;
    private int mouseX;
    private int mouseY;
    private int scroll;
    private double slide, progress = 0;
    public ClickGui() {
        final int width = 100;
        final int height = 18;

        int yPos = 5;
        for (final ModuleCategory category : ModuleCategory.values()) {
            panels.add(new Panel(category.getDisplayName(), 100, yPos, width, height, false) {

                @Override
                public void setupItems() {
                    for (Module module : DragLine.moduleManager.getModules())
                        if (module.getCategory() == category)
                            getElements().add(new ModuleElement(module));
                }
            });

            yPos += 20;
        }

        yPos += 20;

        panels.add(new Panel("Targets", 100, yPos, width, height, false) {

            @Override
            public void setupItems() {
                getElements().add(new ButtonElement("Players") {

                    @Override
                    public void createButton(String displayName) {
                        color = EntityUtils.targetPlayer ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        super.createButton(displayName);
                    }

                    @Override
                    public String getDisplayName() {
                        displayName = "Players";
                        color = EntityUtils.targetPlayer ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        return super.getDisplayName();
                    }

                    @Override
                    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
                        if (mouseButton == 0 && isHovering(mouseX, mouseY) && isVisible()) {
                            EntityUtils.targetPlayer = !EntityUtils.targetPlayer;
                            displayName = "Players";
                            color = EntityUtils.targetPlayer ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                            mc.getSoundHandler().playSound("gui.button.press", 1.0F);
                        }
                    }
                });

                getElements().add(new ButtonElement("Mobs") {

                    @Override
                    public void createButton(String displayName) {
                        color = EntityUtils.targetMobs ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        super.createButton(displayName);
                    }

                    @Override
                    public String getDisplayName() {
                        displayName = "Mobs";
                        color = EntityUtils.targetMobs ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        return super.getDisplayName();
                    }

                    @Override
                    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
                        if (mouseButton == 0 && isHovering(mouseX, mouseY) && isVisible()) {
                            EntityUtils.targetMobs = !EntityUtils.targetMobs;
                            displayName = "Mobs";
                            color = EntityUtils.targetMobs ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                            mc.getSoundHandler().playSound("gui.button.press", 1.0F);
                        }
                    }
                });

                getElements().add(new ButtonElement("Animals") {

                    @Override
                    public void createButton(String displayName) {
                        color = EntityUtils.targetAnimals ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        super.createButton(displayName);
                    }

                    @Override
                    public String getDisplayName() {
                        displayName = "Animals";
                        color = EntityUtils.targetAnimals ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        return super.getDisplayName();
                    }

                    @Override
                    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
                        if (mouseButton == 0 && isHovering(mouseX, mouseY) && isVisible()) {
                            EntityUtils.targetAnimals = !EntityUtils.targetAnimals;
                            displayName = "Animals";
                            color = EntityUtils.targetAnimals ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                            mc.getSoundHandler().playSound("gui.button.press", 1.0F);
                        }
                    }
                });

                getElements().add(new ButtonElement("Invisible") {

                    @Override
                    public void createButton(String displayName) {
                        color = EntityUtils.targetInvisible ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        super.createButton(displayName);
                    }

                    @Override
                    public String getDisplayName() {
                        displayName = "Invisible";
                        color = EntityUtils.targetInvisible ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        return super.getDisplayName();
                    }

                    @Override
                    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
                        if (mouseButton == 0 && isHovering(mouseX, mouseY) && isVisible()) {
                            EntityUtils.targetInvisible = !EntityUtils.targetInvisible;
                            displayName = "Invisible";
                            color = EntityUtils.targetInvisible ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                            mc.getSoundHandler().playSound("gui.button.press", 1.0F);
                        }
                    }
                });

                getElements().add(new ButtonElement("Dead") {

                    @Override
                    public void createButton(String displayName) {
                        color = EntityUtils.targetDead ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        super.createButton(displayName);
                    }

                    @Override
                    public String getDisplayName() {
                        displayName = "Dead";
                        color = EntityUtils.targetDead ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                        return super.getDisplayName();
                    }

                    @Override
                    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
                        if (mouseButton == 0 && isHovering(mouseX, mouseY) && isVisible()) {
                            EntityUtils.targetDead = !EntityUtils.targetDead;
                            displayName = "Dead";
                            color = EntityUtils.targetDead ? ClickGUI.generateColor().getRGB() : Integer.MAX_VALUE;
                            mc.getSoundHandler().playSound("gui.button.press", 1.0F);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (progress < 1) progress += 0.1 * (1 - partialTicks);
        else progress = 1;

        if (Mouse.isButtonDown(0) && mouseX >= 5 && mouseX <= 50 && mouseY <= representedScreen.getHeight() - 5 && mouseY >= representedScreen.getHeight() - 50)
            mc.displayGuiScreen(classProvider.wrapGuiScreen(new GuiHudDesigner()));

        // Enable DisplayList optimization
        //AWTFontRenderer.Companion.setAssumeNonVolatile(true);

        final double scale = ((ClickGUI) Objects.requireNonNull(DragLine.moduleManager.getModule(ClickGUI.class))).scaleValue.get();
        GlStateManager.translate(0, scroll, 0);
        mouseY -= scroll;

        mouseX /= scale;
        mouseY /= scale;

        this.mouseX = mouseX;
        this.mouseY = mouseY;


        representedScreen.drawDefaultBackground();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        RenderUtils.drawRect(0, sr.getScaledHeight(), sr.getScaledWidth(), sr.getScaledHeight() - scroll, -804253680);
//                break;


        GlStateManager.disableAlpha();
        RenderUtils.drawImage(hudIcon, 9, representedScreen.getHeight() - 41, 32, 32);
        GlStateManager.enableAlpha();

        GlStateManager.scale(scale, scale, scale);
        for (final Panel panel : panels) {
            panel.updateFade(RenderUtils.deltaTime);
            panel.drawScreen(mouseX, mouseY, partialTicks);
        }

        for (final Panel panel : panels) {
            for (final Element element : panel.getElements()) {
                if (element instanceof ModuleElement) {
                    final ModuleElement moduleElement = (ModuleElement) element;

                    if (mouseX != 0 && mouseY != 0 && moduleElement.isHovering(mouseX, mouseY) && moduleElement.isVisible() && element.getY() <= panel.getY() + panel.getFade())
                        style.drawDescription(mouseX, mouseY, moduleElement.getModule().getDescription());
                }
            }
        }


        GlStateManager.disableLighting();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.scale(1, 1, 1);
        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();

            for (int i = panels.size() - 1; i >= 0; i--)
                if (panels.get(i).handleScroll(mouseX, mouseY, wheel))
                    return;
            if (wheel < 0) {
                scroll -= 15;
            } else if (wheel > 0) {
                scroll += 15;
                if (scroll > 0) {
                    scroll = 0;
                }
            }
        }

        GlStateManager.scale(1, 1, 1);

        //AWTFontRenderer.Companion.setAssumeNonVolatile(false);

        super.drawScreen(mouseX, mouseY, partialTicks);
        if (clickEffects.size() > 0) {
            Iterator<ClickEffect> clickEffectIterator = clickEffects.iterator();
            while (clickEffectIterator.hasNext()) {
                ClickEffect clickEffect = clickEffectIterator.next();
                clickEffect.draw();
                if (clickEffect.canRemove()) clickEffectIterator.remove();
            }
        }
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        ClickEffect clickEffect = new ClickEffect(mouseX, mouseY);
        clickEffects.add(clickEffect);
        final double scale = ((ClickGUI) Objects.requireNonNull(DragLine.moduleManager.getModule(ClickGUI.class))).scaleValue.get();
        mouseY -= scroll;
        mouseX /= scale;
        mouseY /= scale;

        for (final Panel panel : panels) {
            panel.mouseClicked(mouseX, mouseY, mouseButton);

            panel.drag = false;

            if (mouseButton == 0 && panel.isHovering(mouseX, mouseY))
                clickedPanel = panel;
        }

        if (clickedPanel != null) {
            clickedPanel.x2 = clickedPanel.x - mouseX;
            clickedPanel.y2 = clickedPanel.y - mouseY;
            clickedPanel.drag = true;

            panels.remove(clickedPanel);
            panels.add(clickedPanel);
            clickedPanel = null;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        final double scale = ((ClickGUI) Objects.requireNonNull(DragLine.moduleManager.getModule(ClickGUI.class))).scaleValue.get();

        mouseX /= scale;
        mouseY /= scale;

        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void updateScreen() {
        for (final Panel panel : panels) {
            for (final Element element : panel.getElements()) {
                if (element instanceof ButtonElement) {
                    final ButtonElement buttonElement = (ButtonElement) element;

                    if (buttonElement.isHovering(mouseX, mouseY)) {
                        if (buttonElement.hoverTime < 7)
                            buttonElement.hoverTime++;
                    } else if (buttonElement.hoverTime > 0)
                        buttonElement.hoverTime--;
                }

                if (element instanceof ModuleElement) {
                    if (((ModuleElement) element).getModule().getState()) {
                        if (((ModuleElement) element).slowlyFade < 255)
                            ((ModuleElement) element).slowlyFade += 20;
                    } else if (((ModuleElement) element).slowlyFade > 0)
                        ((ModuleElement) element).slowlyFade -= 20;

                    if (((ModuleElement) element).slowlyFade > 255)
                        ((ModuleElement) element).slowlyFade = 255;

                    if (((ModuleElement) element).slowlyFade < 0)
                        ((ModuleElement) element).slowlyFade = 0;
                }
            }
        }
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        DragLine.fileManager.saveConfig(DragLine.fileManager.clickGuiConfig);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
