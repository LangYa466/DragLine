/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.utils;

import com.google.gson.JsonObject;
import dragline.DragLine;
import dragline.api.minecraft.INetworkManager;
import dragline.api.minecraft.network.login.server.ISPacketEncryptionRequest;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.awt.*;
import java.security.PublicKey;

@SideOnly(Side.CLIENT)
public final class ClientUtils extends MinecraftInstance {

    private static final Logger logger = LogManager.getLogger("LiquidBounce");

    public static Logger getLogger() {
        return logger;
    }

    public static void disableFastRender() {
        DragLine.wrapper.getFunctions().disableFastRender();
    }
    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float)c.getRed();
        float g = 0.003921569f * (float)c.getGreen();
        float b = 0.003921569f * (float)c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }
    public static void sendEncryption(final INetworkManager networkManager, final SecretKey secretKey, final PublicKey publicKey, final ISPacketEncryptionRequest encryptionRequest) {
        networkManager.sendPacket(classProvider.createCPacketEncryptionResponse(secretKey, publicKey, encryptionRequest.getVerifyToken()), () -> {
            networkManager.enableEncryption(secretKey);

            return null;
        });
    }

    public static void displayChatMessage(final String message) {

        if (mc.getThePlayer() == null) {
            getLogger().info("(MCChat)" + message);
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", message);

        mc.getThePlayer().addChatMessage(DragLine.wrapper.getFunctions().jsonToComponent(jsonObject.toString()));
    }
}