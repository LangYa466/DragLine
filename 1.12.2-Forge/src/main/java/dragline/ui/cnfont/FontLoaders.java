package dragline.ui.cnfont;

import dragline.DragLine;
import me.nelly.utils.HttpUtils;
import dragline.features.module.modules.client.CustomFonts;
import dragline.utils.ClientUtils;
import me.nelly.utils.ZipUtils;
import nellyobfuscator.NellyClassObfuscator;
import net.minecraft.util.ResourceLocation;
import top.fl0wowp4rty.phantomshield.annotations.Native;
import top.fl0wowp4rty.phantomshield.annotations.license.RegisterLock;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Native
@RegisterLock
@NellyClassObfuscator
public abstract class FontLoaders {
    public static FontDrawer GenShin;
    public static FontDrawer WerRuanYaHei;
    public static FontDrawer ShouKeSon;
    public static FontDrawer DengXian;
    public static FontDrawer CUSTOM;
    public static FontDrawer CUSTOM18;
    public static FontDrawer CUSTOM14;
    public static FontDrawer CUSTOM35;
    public static FontDrawer CUSTOM40;
    public static FontDrawer CUSTOM64;

    public static void initFonts() {
        long l = System.currentTimeMillis();
        ClientUtils.getLogger().info("Downloading cnfonts...");

        extractZip(new ResourceLocation("dragline/cnfonts.zip").getResourcePath(),DragLine.fileManager.cnfontsDir.getAbsolutePath());

        ClientUtils.getLogger().info("Loading CNFonts...");

        GenShin = getFont("genshin",true);
        WerRuanYaHei = getFont("werruanyahei",true);
        ShouKeSon = getFont("shoukeson",true);
        DengXian = getFont("dengxian",true);

        CUSTOM = getCustomFont((int) CustomFonts.Companion.getSize(),true);
        CUSTOM14 = getCustomFont(14,true);
        CUSTOM18 = getCustomFont(18,true);
        CUSTOM35 = getCustomFont(35,true);
        CUSTOM40 = getCustomFont(40,true);
        CUSTOM64 = getCustomFont(64,true);

        ClientUtils.getLogger().info("Loaded CNFonts. (" + (System.currentTimeMillis() - l) + "ms)");

    }
    private static void extractZip(final String zipFile, final String outputFolder) {
        final byte[] buffer = new byte[1024];

        try {
            final File folder = new File(outputFolder);

            if (!folder.exists()) folder.mkdir();

            final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));

            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(outputFolder + File.separator + zipEntry.getName());
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);

                int i;
                while ((i = zipInputStream.read(buffer)) > 0)
                    fileOutputStream.write(buffer, 0, i);

                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static FontDrawer getFont2(String name,int size,boolean antiAliasing) {
        Font font;
        try {
            font = Font.createFont(0, Files.newInputStream(new File(DragLine.fileManager.cnfontsDir, name + ".ttf").toPath())).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            font = new Font("default", Font.PLAIN, size);
            ex.printStackTrace();
        }
        return new FontDrawer(font, antiAliasing);
    }

    public static FontDrawer getFont(String name,boolean antiAliasing) {
        Font font;
        final int size = (int) CustomFonts.Companion.getSize();
        try {
            font = Font.createFont(0, Files.newInputStream(new File(DragLine.fileManager.cnfontsDir, name + ".ttf").toPath())).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            font = new Font("default", Font.PLAIN, size);
            ex.printStackTrace();
        }
        return new FontDrawer(font, antiAliasing);
    }

    public static FontDrawer getCustomFont(int size,boolean antiAliasing) {
        Font font;
        try {
            font = Font.createFont(0, Files.newInputStream(new File(DragLine.fileManager.cnfontsDir, CustomFonts.Companion.getFont() + ".ttf").toPath())).deriveFont(Font.PLAIN, (float) size);
        } catch (Exception ex) {
            font = new Font("default", Font.PLAIN, size);
            ex.printStackTrace();
        }
        return new FontDrawer(font, antiAliasing);
    }
}
