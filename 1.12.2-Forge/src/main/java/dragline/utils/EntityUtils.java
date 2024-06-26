/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package dragline.utils;

import me.rainyfall.module.player.AntiFakePlayer;
import dragline.DragLine;
import dragline.api.minecraft.client.entity.IEntity;
import dragline.api.minecraft.client.entity.player.IEntityPlayer;
import dragline.api.minecraft.client.network.INetworkPlayerInfo;
import dragline.api.minecraft.scoreboard.ITeam;
import dragline.features.module.Module;
import dragline.features.module.modules.combat.NoFriends;
import dragline.features.module.modules.misc.AntiBot;
import dragline.features.module.modules.misc.Teams;
import dragline.utils.render.ColorUtils;

public final class EntityUtils extends MinecraftInstance {

    public static boolean targetInvisible = false;
    public static boolean targetPlayer = true;
    public static boolean targetMobs = true;
    public static boolean targetAnimals = false;
    public static boolean targetDead = false;

    public static boolean isSelected(final IEntity entity, final boolean canAttackCheck) {
        final Module Antifakeplayer = DragLine.moduleManager.getModule(AntiFakePlayer.class);
        if (classProvider.isEntityLivingBase(entity) && (targetDead || entity.isEntityAlive()) && entity != null && !entity.equals(mc.getThePlayer())) {
            if (targetInvisible || !entity.isInvisible()) {
                if (targetPlayer && classProvider.isEntityPlayer(entity)) {
                    IEntityPlayer entityPlayer = entity.asEntityPlayer();

                    if (canAttackCheck) {
                        if (AntiBot.isBot(entityPlayer))
                            return false;


                        if (isFriend(entityPlayer) && !DragLine.moduleManager.getModule(NoFriends.class).getState())
                            return false;

                        if (entityPlayer.isSpectator())
                            return false;

                        final Teams teams = (Teams) DragLine.moduleManager.getModule(Teams.class);
                        return !teams.getState() || !teams.isInYourTeam(entityPlayer);
                    }

                    return true;
                }

                return targetMobs && isMob(entity) || targetAnimals && isAnimal(entity);

            }
        }
        return false;
    }

    public static boolean isFriend(final IEntity entity) {
        return classProvider.isEntityPlayer(entity) && entity.getName() != null &&
                DragLine.fileManager.friendsConfig.isFriend(ColorUtils.stripColor(entity.getName()));
    }

    public static boolean isAnimal(final IEntity entity) {
        return classProvider.isEntityAnimal(entity) || classProvider.isEntitySquid(entity) || classProvider.isEntityGolem(entity) ||
                classProvider.isEntityBat(entity);
    }

    public static boolean isMob(final IEntity entity) {
        return classProvider.isEntityMob(entity) || classProvider.isEntityVillager(entity) || classProvider.isEntitySlime(entity)
                || classProvider.isEntityGhast(entity) || classProvider.isEntityDragon(entity) || classProvider.isEntityShulker(entity);
    }

    public static String getName(final INetworkPlayerInfo networkPlayerInfoIn) {
        if (networkPlayerInfoIn.getDisplayName() != null)
            return networkPlayerInfoIn.getDisplayName().getFormattedText();

        ITeam team = networkPlayerInfoIn.getPlayerTeam();
        String name = networkPlayerInfoIn.getGameProfile().getName();

        return team == null ? name : team.formatString(name);
    }

    public static int getPing(final IEntityPlayer entityPlayer) {
        if (entityPlayer == null)
            return 0;

        final INetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(entityPlayer.getUniqueID());

        return networkPlayerInfo == null ? 0 : networkPlayerInfo.getResponseTime();
    }
}
