/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.file.configs;

import dragline.DragLine;
import dragline.file.FileConfig;
import dragline.ui.client.hud.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HudConfig extends FileConfig {

    /**
     * Constructor of config
     *
     * @param file of config
     */
    public HudConfig(final File file) {
        super(file);
    }

    /**
     * Load config from file
     *
     * @throws IOException
     */
    @Override
    protected void loadConfig() throws IOException {
        DragLine.hud.clearElements();
        DragLine.hud = new Config(FileUtils.readFileToString(getFile())).toHUD();
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    @Override
    protected void saveConfig() throws IOException {
        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(new Config(DragLine.hud).toJson());
        printWriter.close();
    }
}
