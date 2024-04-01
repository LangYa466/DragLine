/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.ui.font;

import dragline.features.module.modules.client.CustomFonts;
import nellyobfuscator.NellyClassObfuscator;
import dragline.api.minecraft.client.gui.IFontRenderer;
import dragline.utils.ClientUtils;
import dragline.utils.MinecraftInstance;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.*;
@NellyClassObfuscator
public class Fonts extends MinecraftInstance {

    @FontDetails(fontName = "Minecraft Font")
    public static final IFontRenderer minecraftFont = mc.getFontRendererObj();
    private static final HashMap<FontInfo, IFontRenderer> CUSTOM_FONT_RENDERERS = new HashMap<>();
    @FontDetails(fontName = "Roboto Medium", fontSize = 16)
    public static IFontRenderer font16;
    @FontDetails(fontName = "Roboto Medium", fontSize = 18)
    public static IFontRenderer font18;
    @FontDetails(fontName = "Roboto Medium", fontSize = 20)
    public static IFontRenderer font20;
    @FontDetails(fontName = "Roboto Medium", fontSize = 22)
    public static IFontRenderer font22;
    @FontDetails(fontName = "Roboto Medium", fontSize = 25)
    public static IFontRenderer font25;
    @FontDetails(fontName = "Roboto Medium", fontSize = 27)
    public static IFontRenderer font27;
    @FontDetails(fontName = "Roboto Medium", fontSize = 28)
    public static IFontRenderer font28;
    @FontDetails(fontName = "Roboto Medium", fontSize = 30)
    public static IFontRenderer font30;
    @FontDetails(fontName = "Roboto Medium", fontSize = 35)
    public static IFontRenderer font35;
    @FontDetails(fontName = "Roboto Medium", fontSize = 40)
    public static IFontRenderer font40;
    @FontDetails(fontName = "Roboto Medium", fontSize = 52)
    public static IFontRenderer font52;
    @FontDetails(fontName = "Roboto Bold", fontSize = 95)
    public static IFontRenderer fontBold95;
    @FontDetails(fontName = "Roboto Bold", fontSize = 180)
    public static IFontRenderer fontBold180;
    @FontDetails(fontName = "Notion 40", fontSize = 40)
    public static IFontRenderer no40;
    @FontDetails(fontName = "noti", fontSize = 85)
    public static IFontRenderer NOTIFICATIONS;

    @FontDetails(fontName = "noti2", fontSize = 85)
    public static IFontRenderer NOTIFICATIONS2;
    @FontDetails(fontName = "Tahoma Bold", fontSize = 35)
    public static IFontRenderer fontTahoma;

    @FontDetails(fontName = "Tahoma Bold", fontSize = 30)
    public static IFontRenderer fontTahoma30;
    @FontDetails(fontName = "Tahoma Bold", fontSize = 14)
    public static IFontRenderer fontTahoma14;
    @FontDetails(fontName = "Tahoma Bold", fontSize = 18)
    public static IFontRenderer fontTahoma18;
    @FontDetails(fontName = "Ico", fontSize = 20)
    public static IFontRenderer ico1;
    @FontDetails(fontName = "Ico", fontSize = 20)
    public static IFontRenderer ico2;
    public static void loadFonts() {
        long l = System.currentTimeMillis();
        //Roboto-Medium.ttf to sfui.ttf

        ClientUtils.getLogger().info("Loading Fonts.");
        font16 = getFont(CustomFonts.Companion.getenFont(), 16);
        font18 = getFont(CustomFonts.Companion.getenFont(), 18);
        font20 = getFont(CustomFonts.Companion.getenFont(), 20);
        font22 = getFont(CustomFonts.Companion.getenFont(), 22);
        font25 = getFont(CustomFonts.Companion.getenFont(), 25);
        font27 = getFont(CustomFonts.Companion.getenFont(), 27);
        font28 = getFont(CustomFonts.Companion.getenFont(), 28);
        font30 = getFont(CustomFonts.Companion.getenFont(), 30);
        font35 = getFont(CustomFonts.Companion.getenFont(), 35);
        font40 = getFont(CustomFonts.Companion.getenFont(), 40);
        font52 = getFont(CustomFonts.Companion.getenFont(), 52);
        fontBold95 = getFont("Roboto-Bold.ttf", 95);
        fontBold180 = getFont("Roboto-Bold.ttf", 180);
        no40 =  getFont("notion.ttf", 40);
        NOTIFICATIONS = getFont("noti.ttf", 85);
        NOTIFICATIONS2 = getFont("noti2.ttf", 85);
        fontTahoma = getFont("TahomaBold.ttf", 35);
        fontTahoma30 = getFont("TahomaBold.ttf", 30);
        fontTahoma18 = getFont("TahomaBold.ttf", 18);
        fontTahoma14 = getFont("TahomaBold.ttf", 14);
        ico1 = getFont("icon.ttf", 30);
        ico2 = getFont("icon2.ttf", 30);

        ClientUtils.getLogger().info("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }

    public static IFontRenderer getFontRenderer(final String name, final int size) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                Object o = field.get(null);

                if (o instanceof IFontRenderer) {
                    FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    if (fontDetails.fontName().equals(name) && fontDetails.fontSize() == size)
                        return (IFontRenderer) o;
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return CUSTOM_FONT_RENDERERS.getOrDefault(new FontInfo(name, size), minecraftFont);
    }

    public static FontInfo getFontDetails(final IFontRenderer fontRenderer) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if (o.equals(fontRenderer)) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    return new FontInfo(fontDetails.fontName(), fontDetails.fontSize());
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<FontInfo, IFontRenderer> entry : CUSTOM_FONT_RENDERERS.entrySet()) {
            if (entry.getValue() == fontRenderer)
                return entry.getKey();
        }

        return null;
    }

    public static List<IFontRenderer> getFonts() {
        final List<IFontRenderer> fonts = new ArrayList<>();

        for (final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if (fontObj instanceof IFontRenderer) fonts.add((IFontRenderer) fontObj);
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fonts.addAll(Fonts.CUSTOM_FONT_RENDERERS.values());

        return fonts;
    }

    private static IFontRenderer getFont(final String fontName, final int size) {
        Font font;
        try {
            final InputStream inputStream = mc2.getResourceManager().getResource(new ResourceLocation("liquidbounce/font/" + fontName)).getInputStream();
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            font = awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();
            font = new Font("default", Font.PLAIN, size);
        }

        return classProvider.wrapFontRenderer(new GameFontRenderer(font));
    }

    public static class FontInfo {
        private final String name;
        private final int fontSize;

        public FontInfo(String name, int fontSize) {
            this.name = name;
            this.fontSize = fontSize;
        }

        public FontInfo(Font font) {
            this(font.getName(), font.getSize());
        }

        public String getName() {
            return name;
        }

        public int getFontSize() {
            return fontSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FontInfo fontInfo = (FontInfo) o;

            if (fontSize != fontInfo.fontSize) return false;
            return Objects.equals(name, fontInfo.name);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + fontSize;
            return result;
        }
    }
}