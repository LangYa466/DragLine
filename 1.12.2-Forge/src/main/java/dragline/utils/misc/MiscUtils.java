/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.utils.misc;

import dragline.utils.MinecraftInstance;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public final class MiscUtils extends MinecraftInstance {

    public static void showErrorPopup(final String title, final String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showURL(final String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        }catch(final IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static File openFileChooser() {
        if (mc.isFullScreen())
            mc.toggleFullscreen();

        final JFileChooser fileChooser = new JFileChooser();
        final JFrame frame = new JFrame();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);

        final int action = fileChooser.showOpenDialog(frame);
        frame.dispose();

        return action == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }


    public static File saveFileChooser() {
        if (mc.isFullScreen())
            mc.toggleFullscreen();

        final JFileChooser fileChooser = new JFileChooser();
        final JFrame frame = new JFrame();

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        frame.setVisible(true);
        frame.toFront();
        frame.setVisible(false);

        final int action = fileChooser.showSaveDialog(frame);
        frame.dispose();

        return action == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }

    public static boolean movingForward() {
        EntityPlayerSP player = mc2.player;
        if (player == null) {
            return false;
        }

        MovementInput playerMovementInput = player.movementInput;
        Vec3d playerPos = player.getPositionVector();
        double motionX = player.motionX;
        double motionY = player.motionY;
        double motionZ = player.motionZ;

        // Calculate the position of the block in front
        BlockPos blockPos = new BlockPos(
                playerPos.x + motionX,
                playerPos.y + motionY,
                playerPos.z + motionZ
        );
        IBlockState blockState = mc2.world.getBlockState(blockPos);

        // Check if there is a block in front
        if (blockState.getBlock().isAir(blockState, mc2.world, blockPos) || blockState.getBlock().isReplaceable(mc2.world, blockPos)) {
            return false;
        }

        // Check if the player is pressing the forward key
        return playerMovementInput.moveForward > 0.0F;
    }

}
