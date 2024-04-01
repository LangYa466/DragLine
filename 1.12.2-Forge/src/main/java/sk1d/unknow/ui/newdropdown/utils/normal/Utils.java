package sk1d.unknow.ui.newdropdown.utils.normal;

import dragline.DragLine;
import dragline.api.minecraft.client.IMinecraft;
import dragline.api.minecraft.client.gui.IFontRenderer;

public interface Utils {
    IMinecraft mc = DragLine.INSTANCE.getWrapper().getMinecraft();
    IFontRenderer fr = mc.getFontRendererObj();
}
